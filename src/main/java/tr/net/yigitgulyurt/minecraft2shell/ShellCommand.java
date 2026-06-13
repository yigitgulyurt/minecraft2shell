package tr.net.yigitgulyurt.minecraft2shell;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfig;
import tr.net.yigitgulyurt.minecraft2shell.data.HistoryManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class ShellCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        // /shell <komut> - ONLY FOR CONSOLE OR OPS (temporary check)
        dispatcher.register(Commands.literal("shell")
                .requires(source -> source.getEntity() == null)
                .then(Commands.literal("history")
                        .executes(ctx -> showHistory(ctx.getSource()))
                        .then(Commands.literal("clear")
                                .executes(ctx -> clearHistory(ctx.getSource())))
                        .then(Commands.argument("index", IntegerArgumentType.integer(1))
                                .executes(ctx -> runFromHistory(
                                        ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "index")))))

                .then(Commands.literal("alias")
                        .then(Commands.literal("list")
                                .executes(ctx -> listAliases(ctx.getSource())))
                        .then(Commands.literal("add")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .then(Commands.argument("command", StringArgumentType.greedyString())
                                                .executes(ctx -> addAlias(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "name"),
                                                        StringArgumentType.getString(ctx, "command"))))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(ctx -> removeAlias(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name"))))))

                .then(Commands.literal("blacklist")
                        .then(Commands.literal("list")
                                .executes(ctx -> listBlacklist(ctx.getSource())))
                        .then(Commands.literal("add")
                                .then(Commands.argument("entry", StringArgumentType.greedyString())
                                        .executes(ctx -> addBlacklist(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "entry")))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("entry", StringArgumentType.greedyString())
                                        .executes(ctx -> removeBlacklist(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "entry"))))))

                .then(Commands.literal("config")
                        .executes(ctx -> openConfig(ctx.getSource())))

                .then(Commands.argument("cmd", StringArgumentType.greedyString())
                        .executes(ctx -> runCommand(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "cmd"))))
        );
    }

    // --- Alias komutlarini kaydet (/aliasadi seklinde) ---
    public static void registerAliases(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (Map.Entry<String, String> entry : ModConfig.get().aliases.entrySet()) {
            registerSingleAlias(dispatcher, entry.getKey(), entry.getValue());
        }
    }

    public static void registerSingleAlias(CommandDispatcher<CommandSourceStack> dispatcher,
                                            String name, String command) {
        try {
            dispatcher.register(Commands.literal(name)
                    .requires(source -> source.getEntity() == null)
                    .executes(ctx -> runCommand(ctx.getSource(), command)));
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Alias kaydedilemedi '" + name + "': " + e.getMessage());
        }
    }

    // --- /shell <cmd> ---
    public static int runCommand(CommandSourceStack source, String cmd) {
        if (source.getEntity() != null) {
            source.sendFailure(Component.literal("§c[m2s] Bu komut şu anda sadece konsoldan kullanılabilir!"));
            return 0;
        }
        ModConfig cfg = ModConfig.get();

        if (cfg.isBlacklisted(cmd)) {
            source.sendFailure(Component.literal("§c[m2s] Bu komut kara listede: " + cmd));
            return 0;
        }

        HistoryManager.add(cmd);

        new Thread(() -> {
            try {
                Process process = new ProcessBuilder("cmd.exe", "/c", cmd)
                        .redirectErrorStream(true)
                        .start();

                if (cfg.showOutput) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream(), "CP857"));

                    int lineCount = 0;
                    int limit = cfg.outputLineLimit;
                    String line;

                    while ((line = reader.readLine()) != null) {
                        if (lineCount >= limit) {
                            final int remaining = countRemainingLines(reader);
                            source.getServer().execute(() ->
                                    source.sendSuccess(() -> Component.literal(
                                            "§e[m2s] ... ve " + remaining + " satir daha (limit: "
                                                    + limit + "). Limiti /shell config'den artirabilirsin."), false));
                            break;
                        }
                        final String output = line;
                        source.getServer().execute(() ->
                                source.sendSuccess(() -> Component.literal("§7" + output), false));
                        lineCount++;
                    }
                }

                int exitCode = process.waitFor();
                if (cfg.showOutput) {
                    source.getServer().execute(() ->
                            source.sendSuccess(() -> Component.literal(
                                    "§a[m2s] Tamamlandi (kod: " + exitCode + ")"), false));
                }

            } catch (Exception e) {
                source.getServer().execute(() ->
                        source.sendFailure(Component.literal("§c[m2s] Hata: " + e.getMessage())));
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
    private static int showHistory(CommandSourceStack source) {
        List<String> history = HistoryManager.getAll();
        if (history.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§e[m2s] Gecmis bos."), false);
            return 1;
        }
        source.sendSuccess(() -> Component.literal("§6[m2s] Komut gecmisi:"), false);
        for (int i = 0; i < history.size(); i++) {
            final int idx = i + 1;
            final String cmd = history.get(i);
            source.sendSuccess(() -> Component.literal("§7  " + idx + ". " + cmd), false);
        }
        return 1;
    }

    private static int clearHistory(CommandSourceStack source) {
        HistoryManager.clear();
        source.sendSuccess(() -> Component.literal("§a[m2s] Gecmis temizlendi."), false);
        return 1;
    }

    private static int runFromHistory(CommandSourceStack source, int index) {
        List<String> history = HistoryManager.getAll();
        if (index < 1 || index > history.size()) {
            source.sendFailure(Component.literal("§c[m2s] Gecersiz index: " + index));
            return 0;
        }
        String cmd = history.get(index - 1);
        source.sendSuccess(() -> Component.literal("§6[m2s] Calistiriliyor: " + cmd), false);
        return runCommand(source, cmd);
    }

    // --- Alias ---
    private static int listAliases(CommandSourceStack source) {
        Map<String, String> aliases = ModConfig.get().aliases;
        if (aliases.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§e[m2s] Hic alias yok."), false);
            return 1;
        }
        source.sendSuccess(() -> Component.literal("§6[m2s] Alias listesi:"), false);
        aliases.forEach((name, cmd) ->
                source.sendSuccess(() -> Component.literal("§7  /" + name + " -> " + cmd), false));
        return 1;
    }

    private static int addAlias(CommandSourceStack source, String name, String command) {
        ModConfig.get().aliases.put(name, command);
        ModConfig.save();
        source.sendSuccess(() -> Component.literal(
                "§a[m2s] Alias eklendi: /" + name + " -> " + command), false);
        source.sendSuccess(() -> Component.literal(
                "§e[m2s] Alias'in aktif olmasi icin oyunu yeniden baslat."), false);
        return 1;
    }

    private static int removeAlias(CommandSourceStack source, String name) {
        if (ModConfig.get().aliases.remove(name) != null) {
            ModConfig.save();
            source.sendSuccess(() -> Component.literal("§a[m2s] Alias silindi: " + name), false);
            source.sendSuccess(() -> Component.literal(
                    "§e[m2s] Degisiklik icin oyunu yeniden baslat."), false);
        } else {
            source.sendFailure(Component.literal("§c[m2s] Alias bulunamadi: " + name));
        }
        return 1;
    }

    // --- Kara liste ---
    private static int listBlacklist(CommandSourceStack source) {
        List<String> bl = ModConfig.get().blacklist;
        if (bl.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§e[m2s] Kara liste bos."), false);
            return 1;
        }
        source.sendSuccess(() -> Component.literal("§6[m2s] Kara liste:"), false);
        bl.forEach(entry ->
                source.sendSuccess(() -> Component.literal("§7  - " + entry), false));
        return 1;
    }

    private static int addBlacklist(CommandSourceStack source, String entry) {
        ModConfig.get().blacklist.add(entry.trim().toLowerCase());
        ModConfig.save();
        source.sendSuccess(() -> Component.literal("§a[m2s] Kara listeye eklendi: " + entry), false);
        return 1;
    }

    private static int removeBlacklist(CommandSourceStack source, String entry) {
        boolean removed = ModConfig.get().blacklist.remove(entry.trim().toLowerCase());
        if (removed) {
            ModConfig.save();
            source.sendSuccess(() -> Component.literal("§a[m2s] Kara listeden cikarildi: " + entry), false);
        } else {
            source.sendFailure(Component.literal("§c[m2s] Kara listede bulunamadi: " + entry));
        }
        return 1;
    }

    // --- Config ekrani ---
    private static int openConfig(CommandSourceStack source) {
        if (Minecraft2Shell.configOpener != null) {
            Minecraft2Shell.configOpener.open();
        } else {
            source.sendFailure(Component.literal("[m2s] Config ekrani bu ortamda desteklenmiyor."));
        }
        return 1;
    }
}
