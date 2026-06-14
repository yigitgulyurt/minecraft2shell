package tr.net.yigitgulyurt.minecraft2shell;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfig;
import tr.net.yigitgulyurt.minecraft2shell.data.HistoryManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class ShellCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        // /shell <komut> - ONLY CLIENT-SIDE
        dispatcher.register(ClientCommandManager.literal("shell")
                .then(ClientCommandManager.literal("history")
                        .executes(ctx -> showHistory(ctx.getSource()))
                        .then(ClientCommandManager.literal("clear")
                                .executes(ctx -> clearHistory(ctx.getSource())))
                        .then(ClientCommandManager.argument("index", IntegerArgumentType.integer(1))
                                .executes(ctx -> runFromHistory(
                                        ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "index")))))

                .then(ClientCommandManager.literal("alias")
                        .then(ClientCommandManager.literal("list")
                                .executes(ctx -> listAliases(ctx.getSource())))
                        .then(ClientCommandManager.literal("add")
                                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                        .then(ClientCommandManager.argument("command", StringArgumentType.greedyString())
                                                .executes(ctx -> addAlias(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        StringArgumentType.getString(ctx, "command")))))
                        .then(ClientCommandManager.literal("remove")
                                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                        .executes(ctx -> removeAlias(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name")))))

                .then(ClientCommandManager.literal("blacklist")
                        .then(ClientCommandManager.literal("list")
                                .executes(ctx -> listBlacklist(ctx.getSource())))
                        .then(ClientCommandManager.literal("add")
                                .then(ClientCommandManager.argument("entry", StringArgumentType.greedyString())
                                        .executes(ctx -> addBlacklist(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "entry")))))
                        .then(ClientCommandManager.literal("remove")
                                .then(ClientCommandManager.argument("entry", StringArgumentType.greedyString())
                                        .executes(ctx -> removeBlacklist(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "entry")))))

                .then(ClientCommandManager.literal("config")
                        .executes(ctx -> openConfig(ctx.getSource())))

                .then(ClientCommandManager.argument("cmd", StringArgumentType.greedyString())
                        .executes(ctx -> runCommand(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "cmd"))))
        );
    }

    // --- Alias komutlarini kaydet (/aliasadi seklinde) ---
    public static void registerAliases(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        for (Map.Entry<String, String> entry : ModConfig.get().aliases.entrySet()) {
            registerSingleAlias(dispatcher, entry.getKey(), entry.getValue());
        }
    }

    public static void registerSingleAlias(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                            String name, String command) {
        try {
            dispatcher.register(ClientCommandManager.literal(name)
                    .executes(ctx -> runCommand(ctx.getSource(), command)));
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Alias kaydedilemedi '" + name + "': " + e.getMessage());
        }
    }

    // --- /shell <cmd> ---
    public static int runCommand(FabricClientCommandSource source, String cmd) {
        ModConfig cfg = ModConfig.get();

        if (cfg.isBlacklisted(cmd)) {
            source.sendError(Component.literal("§c[m2s] Bu komut kara listede: " + cmd));
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

                    int lineCount = 0;
                    int limit = cfg.outputLineLimit;
                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (lineCount >= limit) {
                            final int remaining = countRemainingLines(reader);
                            source.sendFeedback(Component.literal(
                                    "§e[m2s] ... ve " + remaining + " satir daha (limit: "
                                            + limit + "). Limiti /shell config'den artirabilirsin."));
                            break;
                        }
                        source.sendFeedback(Component.literal("§7" + line));
                        lineCount++;
                    }
                }

                int exitCode = process.waitFor();
                if (cfg.showOutput) {
                    source.sendFeedback(Component.literal(
                            "§a[m2s] Tamamlandi (kod: " + exitCode + ")"));
                }

            } catch (Exception e) {
                source.sendError(Component.literal("§c[m2s] Hata: " + e.getMessage()));
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
            source.sendFeedback(Component.literal("§e[m2s] Gecmis bos."));
            return 1;
        }
        source.sendFeedback(Component.literal("§6[m2s] Komut gecmisi:"));
        for (int i = 0; i < history.size(); i++) {
            final int idx = i + 1;
            final String cmd = history.get(i);
            source.sendFeedback(Component.literal("§7  " + idx + ". " + cmd));
        }
        return 1;
    }

    private static int clearHistory(FabricClientCommandSource source) {
        HistoryManager.clear();
        source.sendFeedback(Component.literal("§a[m2s] Gecmis temizlendi."));
        return 1;
    }

    private static int runFromHistory(FabricClientCommandSource source, int index) {
        List<String> history = HistoryManager.getAll();
        if (index < 1 || index > history.size()) {
            source.sendError(Component.literal("§c[m2s] Gecersiz index: " + index));
            return 0;
        }
        String cmd = history.get(index - 1);
        source.sendFeedback(Component.literal("§6[m2s] Calistiriliyor: " + cmd));
        return runCommand(source, cmd);
    }

    // --- Alias ---
    private static int listAliases(FabricClientCommandSource source) {
        Map<String, String> aliases = ModConfig.get().aliases;
        if (aliases.isEmpty()) {
            source.sendFeedback(Component.literal("§e[m2s] Hic alias yok."));
            return 1;
        }
        source.sendFeedback(Component.literal("§6[m2s] Alias listesi:"));
        aliases.forEach((name, cmd) ->
                source.sendFeedback(Component.literal("§7  /" + name + " -> " + cmd)));
        return 1;
    }

    private static int addAlias(FabricClientCommandSource source, String name, String command) {
        ModConfig.get().aliases.put(name, command);
        ModConfig.save();
        source.sendFeedback(Component.literal(
                "§a[m2s] Alias eklendi: /" + name + " -> " + command));
        source.sendFeedback(Component.literal(
                "§e[m2s] Alias'in aktif olmasi icin oyunu yeniden baslat."));
        return 1;
    }

    private static int removeAlias(FabricClientCommandSource source, String name) {
        if (ModConfig.get().aliases.remove(name) != null) {
            ModConfig.save();
            source.sendFeedback(Component.literal("§a[m2s] Alias silindi: " + name));
            source.sendFeedback(Component.literal(
                    "§e[m2s] Degisiklik icin oyunu yeniden baslat."));
        } else {
            source.sendError(Component.literal("§c[m2s] Alias bulunamadi: " + name));
        }
        return 1;
    }

    // --- Kara liste ---
    private static int listBlacklist(FabricClientCommandSource source) {
        List<String> bl = ModConfig.get().blacklist;
        if (bl.isEmpty()) {
            source.sendFeedback(Component.literal("§e[m2s] Kara liste bos."));
            return 1;
        }
        source.sendFeedback(Component.literal("§6[m2s] Kara liste:"));
        bl.forEach(entry ->
                source.sendFeedback(Component.literal("§7  - " + entry)));
        return 1;
    }

    private static int addBlacklist(FabricClientCommandSource source, String entry) {
        ModConfig.get().blacklist.add(entry.trim().toLowerCase());
        ModConfig.save();
        source.sendFeedback(Component.literal("§a[m2s] Kara listeye eklendi: " + entry));
        return 1;
    }

    private static int removeBlacklist(FabricClientCommandSource source, String entry) {
        boolean removed = ModConfig.get().blacklist.remove(entry.trim().toLowerCase());
        if (removed) {
            ModConfig.save();
            source.sendFeedback(Component.literal("§a[m2s] Kara listeden cikarildi: " + entry));
        } else {
            source.sendError(Component.literal("§c[m2s] Kara listede bulunamadi: " + entry));
        }
        return 1;
    }

    // --- Config ekrani ---
    private static int openConfig(FabricClientCommandSource source) {
        if (Minecraft2Shell.configOpener != null) {
            Minecraft2Shell.configOpener.open();
        } else {
            source.sendError(Component.literal("[m2s] Config ekrani bu ortamda desteklenmiyor."));
        }
        return 1;
    }
}
