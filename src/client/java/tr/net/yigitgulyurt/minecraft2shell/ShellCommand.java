package tr.net.yigitgulyurt.minecraft2shell;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import tr.net.yigitgulyurt.minecraft2shell.command.BlacklistTypeArgument;
import tr.net.yigitgulyurt.minecraft2shell.config.ConfigManager;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfig;
import tr.net.yigitgulyurt.minecraft2shell.data.HistoryManager;
import tr.net.yigitgulyurt.minecraft2shell.data.LanguageManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShellCommand {
    private static String pendingCommand = null;
    private static String pendingSaveFilename = null;
    private static Integer pendingScheduleSeconds = null;
    private static String pendingScheduleCommand = null;
    private static CommandDispatcher<FabricClientCommandSource> currentDispatcher = null;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        currentDispatcher = dispatcher;

        LiteralArgumentBuilder<FabricClientCommandSource> m2sCommand = LiteralArgumentBuilder.<FabricClientCommandSource>literal("m2s")
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("history")
                        .executes(ctx -> showHistory(ctx.getSource(), null))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("clear")
                                .executes(ctx -> clearHistory(ctx.getSource())))
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, Integer>argument("index", IntegerArgumentType.integer(1))
                                .executes(ctx -> runFromHistory(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "index"))))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("search")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("query", StringArgumentType.greedyString())
                                        .executes(ctx -> showHistory(ctx.getSource(), StringArgumentType.getString(ctx, "query"))))))
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("alias")
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("list")
                                .executes(ctx -> listAliases(ctx.getSource())))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("add")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.word())
                                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("command", StringArgumentType.greedyString())
                                                .executes(ctx -> addAlias(ctx.getSource(), StringArgumentType.getString(ctx, "name"), StringArgumentType.getString(ctx, "command"))))))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("remove")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.word())
                                        .executes(ctx -> removeAlias(ctx.getSource(), StringArgumentType.getString(ctx, "name")))))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("run")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.word())
                                        .executes(ctx -> runAlias(ctx.getSource(), StringArgumentType.getString(ctx, "name"))))))
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("blacklist")
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("list")
                                .executes(ctx -> listBlacklist(ctx.getSource())))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("add")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("type", BlacklistTypeArgument.blacklistType())
                                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("pattern", StringArgumentType.greedyString())
                                                .executes(ctx -> addBlacklist(ctx.getSource(), BlacklistTypeArgument.getBlacklistType(ctx, "type"), StringArgumentType.getString(ctx, "pattern"))))))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("remove")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("pattern", StringArgumentType.greedyString())
                                        .executes(ctx -> removeBlacklist(ctx.getSource(), StringArgumentType.getString(ctx, "pattern"))))))
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("config")
                        .executes(ctx -> openConfig(ctx.getSource()))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("export")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("path", StringArgumentType.greedyString())
                                        .executes(ctx -> exportConfig(ctx.getSource(), StringArgumentType.getString(ctx, "path")))))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("import")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("path", StringArgumentType.greedyString())
                                        .executes(ctx -> importConfig(ctx.getSource(), StringArgumentType.getString(ctx, "path"))))))
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("save")
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("filename", StringArgumentType.word())
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("command", StringArgumentType.greedyString())
                                        .executes(ctx -> saveCommandOutput(ctx.getSource(), StringArgumentType.getString(ctx, "filename"), StringArgumentType.getString(ctx, "command"))))))
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("schedule")
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, Integer>argument("seconds", IntegerArgumentType.integer(1))
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("command", StringArgumentType.greedyString())
                                        .executes(ctx -> scheduleCommand(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "seconds"), StringArgumentType.getString(ctx, "command"))))))
                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("cmd", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String cmd = StringArgumentType.getString(ctx, "cmd");
                            if (cmd.equals("confirm")) {
                                return confirmCommand(ctx.getSource());
                            } else if (cmd.equals("cancel")) {
                                return cancelCommand(ctx.getSource());
                            }
                            return runCommand(ctx.getSource(), cmd);
                        }));

        dispatcher.register(m2sCommand);
    }

    public static void registerAliases(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        if (!ConfigManager.getConfig().autoRegisterAliases) return;
        for (Map.Entry<String, String> entry : ConfigManager.getAliases().entrySet()) {
            registerSingleAlias(dispatcher, entry.getKey(), entry.getValue());
        }
    }

    public static void registerSingleAlias(CommandDispatcher<FabricClientCommandSource> dispatcher, String name, String command) {
        try {
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(name)
                    .executes(ctx -> runCommand(ctx.getSource(), command)));
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Alias kaydedilemedi '" + name + ": " + e.getMessage());
        }
    }

    private static void registerAliasDynamically(String name, String command) {
        if (currentDispatcher != null && ConfigManager.getConfig().autoRegisterAliases) {
            registerSingleAlias(currentDispatcher, name, command);
        }
    }

    public static int runCommand(FabricClientCommandSource source, String cmd) {
        return runCommandInternal(source, cmd, null);
    }

    private static int saveCommandOutput(FabricClientCommandSource source, String filename, String cmd) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();

        if (ConfigManager.isBlacklisted(cmd)) {
            source.sendError(Component.literal(theme.error + LanguageManager.get("command.blacklist.error") + cmd));
            return 0;
        }

        if (ConfigManager.getConfig().confirmCommand) {
            pendingCommand = cmd;
            pendingSaveFilename = filename;
            sendConfirmMessage(source, cmd);
            return 1;
        }

        HistoryManager.add(cmd);

        new Thread(() -> {
            try {
                Process process;
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    process = new ProcessBuilder("cmd.exe", "/c", cmd).redirectErrorStream(true).start();
                } else {
                    process = new ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start();
                }

                List<String> allLines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), os.contains("win") ? "CP857" : "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    allLines.add(line);
                }

                Path outputPath = ConfigManager.getOutputPath(filename);
                try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                    for (String l : allLines) {
                        writer.write(l);
                        writer.newLine();
                    }
                }

                ConfigManager.Theme finalTheme = ConfigManager.getCurrentTheme();
                net.minecraft.client.Minecraft.getInstance().execute(() -> {
                    source.sendFeedback(Component.literal(finalTheme.prefix + " " + finalTheme.success + "Output saved to: " + outputPath.toAbsolutePath()));
                });

                int exitCode = process.waitFor();

                if (ConfigManager.getConfig().showOutput) {
                    ModConfig cfg = ConfigManager.getConfig();
                    ConfigManager.Theme finalTheme1 = ConfigManager.getCurrentTheme();
                    int limit = cfg.outputLineLimit;
                    List<String> linesToShow;
                    if (allLines.size() <= limit) {
                        linesToShow = allLines;
                    } else {
                        int remaining;
                        if (cfg.outputReverse) {
                            linesToShow = allLines.subList(allLines.size() - limit, allLines.size());
                            remaining = allLines.size() - limit;
                        } else {
                            linesToShow = allLines.subList(0, limit);
                            remaining = allLines.size() - limit;
                        }
                        final int finalRemaining = remaining;
                        net.minecraft.client.Minecraft.getInstance().execute(() -> {
                            source.sendFeedback(Component.literal(finalTheme1.prefix + " " + finalTheme1.info + LanguageManager.get("command.output.remaining", finalRemaining, limit)));
                        });
                    }
                    net.minecraft.client.Minecraft.getInstance().execute(() -> {
                        for (String outputLine : linesToShow) {
                            source.sendFeedback(Component.literal(finalTheme1.output + outputLine));
                        }
                    });
                }

            } catch (Exception e) {
                ConfigManager.Theme theme1 = ConfigManager.getCurrentTheme();
                net.minecraft.client.Minecraft.getInstance().execute(() -> {
                    source.sendError(Component.literal(theme1.error + getBetterErrorMessage(e)));
                });
            }
        }).start();

        return 1;
    }

    private static void sendConfirmMessage(FabricClientCommandSource source, String cmd) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        
        Component title = Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.confirm.title") + cmd);
        
        Component confirmButton = Component.literal("[Confirm]")
                .setStyle(net.minecraft.network.chat.Style.EMPTY
                        .withColor(net.minecraft.ChatFormatting.GREEN)
                        .withClickEvent(new net.minecraft.network.chat.ClickEvent.RunCommand("/m2s confirm"))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent.ShowText(Component.literal("Click to confirm"))));
        
        Component cancelButton = Component.literal("[Cancel]")
                .setStyle(net.minecraft.network.chat.Style.EMPTY
                        .withColor(net.minecraft.ChatFormatting.RED)
                        .withClickEvent(new net.minecraft.network.chat.ClickEvent.RunCommand("/m2s cancel"))
                        .withHoverEvent(new net.minecraft.network.chat.HoverEvent.ShowText(Component.literal("Click to cancel"))));
        
        source.sendFeedback(title);
        source.sendFeedback(Component.literal(theme.prefix + " ").append(confirmButton).append(" ").append(cancelButton));
    }

    private static int runCommandInternal(FabricClientCommandSource source, String cmd, String savePath) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();

        if (ConfigManager.isBlacklisted(cmd)) {
            source.sendError(Component.literal(theme.error + LanguageManager.get("command.blacklist.error") + cmd));
            return 0;
        }

        if (ConfigManager.getConfig().confirmCommand) {
            pendingCommand = cmd;
            pendingSaveFilename = savePath;
            sendConfirmMessage(source, cmd);
            return 1;
        }

        HistoryManager.add(cmd);

        new Thread(() -> {
            try {
                Process process;
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    process = new ProcessBuilder("cmd.exe", "/c", cmd).redirectErrorStream(true).start();
                } else {
                    process = new ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start();
                }

                List<String> allLines = new ArrayList<>();
                if (ConfigManager.getConfig().showOutput || savePath != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), os.contains("win") ? "CP857" : "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        allLines.add(line);
                    }
                }

                if (savePath != null) {
                    try {
                        Path outputPath = ConfigManager.getOutputPath(savePath);
                        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                            for (String l : allLines) {
                                writer.write(l);
                                writer.newLine();
                            }
                        }
                        ConfigManager.Theme finalTheme = ConfigManager.getCurrentTheme();
                        final String finalPathStr = outputPath.toAbsolutePath().toString();
                        net.minecraft.client.Minecraft.getInstance().execute(() -> {
                            source.sendFeedback(Component.literal(finalTheme.prefix + " " + finalTheme.success + "Output saved to: " + finalPathStr));
                        });
                    } catch (Exception e) {
                        ConfigManager.Theme finalTheme = ConfigManager.getCurrentTheme();
                        final String finalPathStr = savePath;
                        net.minecraft.client.Minecraft.getInstance().execute(() -> {
                            source.sendError(Component.literal(finalTheme.error + "File could not be saved: " + finalPathStr + " - " + e.getMessage()));
                        });
                    }
                }

                if (ConfigManager.getConfig().showOutput) {
                    ModConfig cfg = ConfigManager.getConfig();
                    int limit = cfg.outputLineLimit;
                    List<String> linesToShow;
                    if (allLines.size() <= limit) {
                        linesToShow = allLines;
                    } else {
                        int remaining;
                        if (cfg.outputReverse) {
                            linesToShow = allLines.subList(allLines.size() - limit, allLines.size());
                            remaining = allLines.size() - limit;
                        } else {
                            linesToShow = allLines.subList(0, limit);
                            remaining = allLines.size() - limit;
                        }
                        final int finalRemaining = remaining;
                        ConfigManager.Theme finalTheme = ConfigManager.getCurrentTheme();
                        net.minecraft.client.Minecraft.getInstance().execute(() -> {
                            source.sendFeedback(Component.literal(finalTheme.prefix + " " + finalTheme.info + LanguageManager.get("command.output.remaining", finalRemaining, limit)));
                        });
                    }
                    ConfigManager.Theme finalTheme = ConfigManager.getCurrentTheme();
                    net.minecraft.client.Minecraft.getInstance().execute(() -> {
                        for (String outputLine : linesToShow) {
                            source.sendFeedback(Component.literal(finalTheme.output + outputLine));
                        }
                    });
                }

                int exitCode = process.waitFor();
                if (ConfigManager.getConfig().showOutput) {
                    ConfigManager.Theme finalTheme = ConfigManager.getCurrentTheme();
                    net.minecraft.client.Minecraft.getInstance().execute(() -> {
                        source.sendFeedback(Component.literal(finalTheme.prefix + " " + finalTheme.success + LanguageManager.get("command.output.success", exitCode)));
                    });
                }

            } catch (Exception e) {
                ConfigManager.Theme theme1 = ConfigManager.getCurrentTheme();
                net.minecraft.client.Minecraft.getInstance().execute(() -> {
                    source.sendError(Component.literal(theme1.error + getBetterErrorMessage(e)));
                });
            }
        }).start();

        return 1;
    }

    private static int confirmCommand(FabricClientCommandSource source) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        if (pendingCommand != null) {
            String cmd = pendingCommand;
            String savePath = pendingSaveFilename;
            pendingCommand = null;
            pendingSaveFilename = null;
            ModConfig cfg = ConfigManager.getConfig();
            boolean oldConfirm = cfg.confirmCommand;
            cfg.confirmCommand = false;
            int result = runCommandInternal(source, cmd, savePath);
            cfg.confirmCommand = oldConfirm;
            return result;
        } else if (pendingScheduleSeconds != null && pendingScheduleCommand != null) {
            int seconds = pendingScheduleSeconds;
            String command = pendingScheduleCommand;
            pendingScheduleSeconds = null;
            pendingScheduleCommand = null;
            startSchedule(source, seconds, command);
            return 1;
        } else {
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.confirm.no_pending")));
            return 0;
        }
    }

    private static int cancelCommand(FabricClientCommandSource source) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        if (pendingCommand != null) {
            pendingCommand = null;
            pendingSaveFilename = null;
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.error + LanguageManager.get("command.confirm.cancelled")));
            return 1;
        } else if (pendingScheduleSeconds != null && pendingScheduleCommand != null) {
            pendingScheduleSeconds = null;
            pendingScheduleCommand = null;
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.error + LanguageManager.get("command.confirm.cancelled")));
            return 1;
        } else {
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.confirm.no_pending")));
            return 0;
        }
    }

    private static String getBetterErrorMessage(Exception e) {
        String message = e.getMessage();
        if (message == null) message = e.getClass().getSimpleName();

        if (message.toLowerCase().contains("cannot run program")) {
            return "Command could not be run - is the command correct?";
        } else if (message.toLowerCase().contains("access denied") || message.toLowerCase().contains("permission denied")) {
            return "Access denied - you don't have permission";
        } else if (message.toLowerCase().contains("no such file") || message.toLowerCase().contains("dosya yok")) {
            return "File/directory not found";
        } else {
            return LanguageManager.get("command.error") + message;
        }
    }

    private static int showHistory(FabricClientCommandSource source, String query) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        List<HistoryManager.HistoryEntry> history;
        if (query != null) {
            history = HistoryManager.search(query);
            if (history.isEmpty()) {
                source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.history.no_results")));
                return 1;
            }
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.history.search", query)));
        } else {
            history = HistoryManager.getAll();
            if (history.isEmpty()) {
                source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.history.empty")));
                return 1;
            }
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.history.title")));
        }

        for (int i = 0; i < history.size(); i++) {
            final int idx = i + 1;
            final HistoryManager.HistoryEntry entry = history.get(i);
            source.sendFeedback(Component.literal(theme.output + " " + idx + ". [" + entry.getFormattedTimestamp() + "] " + entry.command));
        }
        return 1;
    }

    private static int clearHistory(FabricClientCommandSource source) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        HistoryManager.clear();
        source.sendFeedback(Component.literal(theme.prefix + " " + theme.success + LanguageManager.get("command.history.cleared")));
        return 1;
    }

    private static int runFromHistory(FabricClientCommandSource source, int index) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        List<HistoryManager.HistoryEntry> history = HistoryManager.getAll();
        if (index < 1 || index > history.size()) {
            source.sendError(Component.literal(theme.error + LanguageManager.get("command.history.invalid_index") + index));
            return 0;
        }
        String cmd = history.get(index - 1).command;
        source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.history.executing") + cmd));
        return runCommand(source, cmd);
    }

    private static int listAliases(FabricClientCommandSource source) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        Map<String, String> aliases = ConfigManager.getAliases();
        if (aliases.isEmpty()) {
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.alias.none")));
            return 1;
        }
        source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.alias.title")));
        aliases.forEach((name, cmd) ->
                source.sendFeedback(Component.literal(theme.output + " /" + name + " -> " + cmd)));
        return 1;
    }

    private static int runAlias(FabricClientCommandSource source, String name) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        Map<String, String> aliases = ConfigManager.getAliases();
        if (!aliases.containsKey(name)) {
            source.sendError(Component.literal(theme.error + LanguageManager.get("command.alias.not_found") + name));
            return 0;
        }
        String cmd = aliases.get(name);
        source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.alias.ran") + cmd));
        return runCommand(source, cmd);
    }

    private static int addAlias(FabricClientCommandSource source, String name, String command) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        ConfigManager.addAlias(name, command);
        source.sendFeedback(Component.literal(theme.prefix + " " + theme.success + LanguageManager.get("command.alias.added") + " /" + name + " -> " + command));
        registerAliasDynamically(name, command);
        return 1;
    }

    private static int removeAlias(FabricClientCommandSource source, String name) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        if (ConfigManager.getAliases().containsKey(name)) {
            ConfigManager.removeAlias(name);
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.success + LanguageManager.get("command.alias.removed") + name));
        } else {
            source.sendError(Component.literal(theme.error + LanguageManager.get("command.alias.not_found") + name));
        }
        return 1;
    }

    private static int listBlacklist(FabricClientCommandSource source) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        List<ConfigManager.BlacklistEntry> bl = ConfigManager.getBlacklist();
        if (bl.isEmpty()) {
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.blacklist.empty")));
            return 1;
        }
        source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + LanguageManager.get("command.blacklist.title")));
        for (ConfigManager.BlacklistEntry entry : bl) {
            String typeLabel = entry.type.equals("specific") ? "Specific" : entry.type.equals("wildcard") ? "Wildcard" : "Regex";
            source.sendFeedback(Component.literal(theme.output + " - [" + typeLabel + "] " + entry.pattern));
        }
        return 1;
    }

    private static int addBlacklist(FabricClientCommandSource source, String type, String pattern) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        String lowerType = type.toLowerCase();
        if (!lowerType.equals("specific") && !lowerType.equals("wildcard") && !lowerType.equals("regex")) {
            source.sendError(Component.literal(theme.error + "Invalid type! Use: specific, wildcard, or regex"));
            return 0;
        }
        ConfigManager.addToBlacklist(lowerType, pattern);
        source.sendFeedback(Component.literal(theme.prefix + " " + theme.success + LanguageManager.get("command.blacklist.added") + " [" + lowerType + "] " + pattern));
        return 1;
    }

    private static int removeBlacklist(FabricClientCommandSource source, String pattern) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        boolean removed = ConfigManager.removeFromBlacklist(pattern);
        if (removed) {
            source.sendFeedback(Component.literal(theme.prefix + " " + theme.success + LanguageManager.get("command.blacklist.removed") + pattern));
        } else {
            source.sendError(Component.literal(theme.error + LanguageManager.get("command.blacklist.not_found") + pattern));
        }
        return 1;
    }

    private static int openConfig(FabricClientCommandSource source) {
        if (Minecraft2Shell.configOpener != null) {
            Minecraft2Shell.configOpener.open();
        } else {
            ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
            source.sendError(Component.literal(theme.error + LanguageManager.get("command.config.not_supported")));
        }
        return 1;
    }

    private static int exportConfig(FabricClientCommandSource source, String pathStr) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        try {
            Path path = Path.of(pathStr);
            boolean success = ConfigManager.exportConfig(path);
            if (success) {
                source.sendFeedback(Component.literal(theme.prefix + " " + theme.success + "Config dışa aktarıldı: " + path.toAbsolutePath()));
            } else {
                source.sendError(Component.literal(theme.error + "Config dışa aktarılamadı!"));
            }
        } catch (Exception e) {
            source.sendError(Component.literal(theme.error + "Geçersiz yol: " + pathStr));
        }
        return 1;
    }

    private static int importConfig(FabricClientCommandSource source, String pathStr) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        try {
            Path path = Path.of(pathStr);
            boolean success = ConfigManager.importConfig(path);
            if (success) {
                source.sendFeedback(Component.literal(theme.prefix + " " + theme.success + "Config içe aktarıldı!"));
                if (currentDispatcher != null) {
                    registerAliases(currentDispatcher);
                }
            } else {
                source.sendError(Component.literal(theme.error + "Config içe aktarılamadı!"));
            }
        } catch (Exception e) {
            source.sendError(Component.literal(theme.error + "Geçersiz yol: " + pathStr));
        }
        return 1;
    }

    private static int scheduleCommand(FabricClientCommandSource source, int seconds, String command) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        if (ConfigManager.getConfig().confirmCommand) {
            pendingScheduleSeconds = seconds;
            pendingScheduleCommand = command;
            sendConfirmMessage(source, "Schedule: " + command + " (" + seconds + "s)");
            return 1;
        }
        startSchedule(source, seconds, command);
        return 1;
    }

    private static void startSchedule(FabricClientCommandSource source, int seconds, String command) {
        ConfigManager.Theme theme = ConfigManager.getCurrentTheme();
        source.sendFeedback(Component.literal(theme.prefix + " " + theme.info + "Komut " + seconds + " saniye sonra çalıştırılacak: " + command));

        new Thread(() -> {
            try {
                Thread.sleep(seconds * 1000L);
                net.minecraft.client.Minecraft.getInstance().execute(() -> {
                    // Schedule için onay zaten alındığı için tekrar onay istemiyoruz
                    ModConfig cfg = ConfigManager.getConfig();
                    boolean oldConfirm = cfg.confirmCommand;
                    cfg.confirmCommand = false;
                    runCommand(source, command);
                    cfg.confirmCommand = oldConfirm;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
