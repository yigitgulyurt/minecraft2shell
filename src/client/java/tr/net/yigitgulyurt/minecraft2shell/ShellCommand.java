package tr.net.yigitgulyurt.minecraft2shell;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfig;
import tr.net.yigitgulyurt.minecraft2shell.data.HistoryManager;
import tr.net.yigitgulyurt.minecraft2shell.data.LanguageManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShellCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        // /m2s <komut> - ONLY CLIENT-SIDE
        LiteralArgumentBuilder<FabricClientCommandSource> m2sCommand = LiteralArgumentBuilder.<FabricClientCommandSource>literal("m2s")
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("history")
                        .executes(ctx -> showHistory(ctx.getSource()))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("clear")
                                .executes(ctx -> clearHistory(ctx.getSource())))
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, Integer>argument("index", IntegerArgumentType.integer(1))
                                .executes(ctx -> runFromHistory(
                                        ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "index")))))

                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("alias")
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("list")
                                .executes(ctx -> listAliases(ctx.getSource())))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("add")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.word())
                                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("command", StringArgumentType.greedyString())
                                                .executes(ctx -> addAlias(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        StringArgumentType.getString(ctx, "command"))))))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("remove")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.word())
                                        .executes(ctx -> removeAlias(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name")))))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("run")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("name", StringArgumentType.word())
                                        .executes(ctx -> runAlias(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name"))))))

                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("blacklist")
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("list")
                                .executes(ctx -> listBlacklist(ctx.getSource())))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("add")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("entry", StringArgumentType.greedyString())
                                        .executes(ctx -> addBlacklist(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "entry")))))
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("remove")
                                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("entry", StringArgumentType.greedyString())
                                        .executes(ctx -> removeBlacklist(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "entry"))))))

                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("config")
                        .executes(ctx -> openConfig(ctx.getSource())))

                .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("cmd", StringArgumentType.greedyString())
                        .executes(ctx -> runCommand(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "cmd"))));

        // Register /m2s command
        dispatcher.register(m2sCommand);
    }

    // --- Alias komutlarini kaydet (/aliasadi seklinde) ---
    public static void registerAliases(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        if (!ModConfig.get().autoRegisterAliases) return;
        for (Map.Entry<String, String> entry : ModConfig.get().aliases.entrySet()) {
            registerSingleAlias(dispatcher, entry.getKey(), entry.getValue());
        }
    }

    public static void registerSingleAlias(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                            String name, String command) {
        try {
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(name)
                    .executes(ctx -> runCommand(ctx.getSource(), command)));
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Alias kaydedilemedi '" + name + "': " + e.getMessage());
        }
    }

    // --- /shell <cmd> ---
    public static int runCommand(FabricClientCommandSource source, String cmd) {
        ModConfig cfg = ModConfig.get();

        if (cfg.isBlacklisted(cmd)) {
            source.sendError(Component.literal(LanguageManager.get("command.blacklist.error") + cmd));
            return 0;
        }

        HistoryManager.add(cmd);

        new Thread(() -> {
            try {
                Process process;
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    process = new ProcessBuilder("cmd.exe", "/c", cmd)
                            .redirectErrorStream(true)
                            .start();
                } else {
                    // Linux/Mac desteği
                    process = new ProcessBuilder("sh", "-c", cmd)
                            .redirectErrorStream(true)
                            .start();
                }

                if (cfg.showOutput) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream(), os.contains("win") ? "CP857" : "UTF-8"));

                    List<String> allLines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        allLines.add(line);
                    }

                    int limit = cfg.outputLineLimit;
                    List<String> linesToShow;
                    if (allLines.size() <= limit) {
                        linesToShow = allLines;
                    } else {
                        final int remaining;
                        if (cfg.outputReverse) {
                            // Sondan göster
                            linesToShow = allLines.subList(allLines.size() - limit, allLines.size());
                            remaining = allLines.size() - limit;
                        } else {
                            // Baştan göster
                            linesToShow = allLines.subList(0, limit);
                            remaining = allLines.size() - limit;
                        }
                        // Mesajları render thread'inde gönder
                        net.minecraft.client.Minecraft.getInstance().execute(() -> {
                            source.sendFeedback(Component.literal(
                                    LanguageManager.get("command.output.remaining", remaining, limit)));
                        });
                    }

                    // Çıktı satırlarını render thread'inde gönder
                    net.minecraft.client.Minecraft.getInstance().execute(() -> {
                        for (String outputLine : linesToShow) {
                            source.sendFeedback(Component.literal("§7" + outputLine));
                        }
                    });
                }

                int exitCode = process.waitFor();
                if (cfg.showOutput) {
                    net.minecraft.client.Minecraft.getInstance().execute(() -> {
                        source.sendFeedback(Component.literal(
                                LanguageManager.get("command.output.success", exitCode)));
                    });
                }

            } catch (Exception e) {
                net.minecraft.client.Minecraft.getInstance().execute(() -> {
                    source.sendError(Component.literal(LanguageManager.get("command.error") + e.getMessage()));
                });
            }
        }).start();

        return 1;
    }

    private static int countRemainingLines(BufferedReader reader) {
        int count = 0;
        try {
            while (reader.readLine() != null) count++;
        } catch (Exception ignored) {}
        return count;
    }

    // --- Gecmis ---
    private static int showHistory(FabricClientCommandSource source) {
        List<String> history = HistoryManager.getAll();
        if (history.isEmpty()) {
            source.sendFeedback(Component.literal(LanguageManager.get("command.history.empty")));
            return 1;
        }
        source.sendFeedback(Component.literal(LanguageManager.get("command.history.title")));
        for (int i = 0; i < history.size(); i++) {
            final int idx = i + 1;
            final String cmd = history.get(i);
            source.sendFeedback(Component.literal("§7  " + idx + ". " + cmd));
        }
        return 1;
    }

    private static int clearHistory(FabricClientCommandSource source) {
        HistoryManager.clear();
        source.sendFeedback(Component.literal(LanguageManager.get("command.history.cleared")));
        return 1;
    }

    private static int runFromHistory(FabricClientCommandSource source, int index) {
        List<String> history = HistoryManager.getAll();
        if (index < 1 || index > history.size()) {
            source.sendError(Component.literal(LanguageManager.get("command.history.invalid_index") + index));
            return 0;
        }
        String cmd = history.get(index - 1);
        source.sendFeedback(Component.literal(LanguageManager.get("command.history.executing") + cmd));
        return runCommand(source, cmd);
    }

    // --- Alias ---
    private static int listAliases(FabricClientCommandSource source) {
        Map<String, String> aliases = ModConfig.get().aliases;
        if (aliases.isEmpty()) {
            source.sendFeedback(Component.literal(LanguageManager.get("command.alias.none")));
            return 1;
        }
        source.sendFeedback(Component.literal(LanguageManager.get("command.alias.title")));
        aliases.forEach((name, cmd) ->
                source.sendFeedback(Component.literal("§7  /" + name + " -> " + cmd)));
        return 1;
    }

    private static int runAlias(FabricClientCommandSource source, String name) {
        Map<String, String> aliases = ModConfig.get().aliases;
        if (!aliases.containsKey(name)) {
            source.sendError(Component.literal(LanguageManager.get("command.alias.not_found") + name));
            return 0;
        }
        String cmd = aliases.get(name);
        source.sendFeedback(Component.literal(LanguageManager.get("command.alias.ran") + cmd));
        return runCommand(source, cmd);
    }

    private static int addAlias(FabricClientCommandSource source, String name, String command) {
        ModConfig.get().aliases.put(name, command);
        ModConfig.save();
        source.sendFeedback(Component.literal(
                LanguageManager.get("command.alias.added") + name + " -> " + command));
        if (ModConfig.get().autoRegisterAliases) {
            source.sendFeedback(Component.literal(
                    LanguageManager.get("command.alias.needs_restart")));
        }
        return 1;
    }

    private static int removeAlias(FabricClientCommandSource source, String name) {
        if (ModConfig.get().aliases.remove(name) != null) {
            ModConfig.save();
            source.sendFeedback(Component.literal(LanguageManager.get("command.alias.removed") + name));
            if (ModConfig.get().autoRegisterAliases) {
                source.sendFeedback(Component.literal(
                        LanguageManager.get("command.alias.needs_restart")));
            }
        } else {
            source.sendError(Component.literal(LanguageManager.get("command.alias.not_found") + name));
        }
        return 1;
    }

    // --- Kara liste ---
    private static int listBlacklist(FabricClientCommandSource source) {
        List<String> bl = ModConfig.get().blacklist;
        if (bl.isEmpty()) {
            source.sendFeedback(Component.literal(LanguageManager.get("command.blacklist.empty")));
            return 1;
        }
        source.sendFeedback(Component.literal(LanguageManager.get("command.blacklist.title")));
        bl.forEach(entry ->
                source.sendFeedback(Component.literal("§7  - " + entry)));
        return 1;
    }

    private static int addBlacklist(FabricClientCommandSource source, String entry) {
        ModConfig.get().blacklist.add(entry.trim().toLowerCase());
        ModConfig.save();
        source.sendFeedback(Component.literal(LanguageManager.get("command.blacklist.added") + entry));
        return 1;
    }

    private static int removeBlacklist(FabricClientCommandSource source, String entry) {
        boolean removed = ModConfig.get().blacklist.remove(entry.trim().toLowerCase());
        if (removed) {
            ModConfig.save();
            source.sendFeedback(Component.literal(LanguageManager.get("command.blacklist.removed") + entry));
        } else {
            source.sendError(Component.literal(LanguageManager.get("command.blacklist.not_found") + entry));
        }
        return 1;
    }

    // --- Config ekrani ---
    private static int openConfig(FabricClientCommandSource source) {
        if (Minecraft2Shell.configOpener != null) {
            Minecraft2Shell.configOpener.open();
        } else {
            source.sendError(Component.literal(LanguageManager.get("command.config.not_supported")));
        }
        return 1;
    }
}
