package tr.net.yigitgulyurt.minecraft2shell.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import tr.net.yigitgulyurt.minecraft2shell.data.HistoryManager;
import tr.net.yigitgulyurt.minecraft2shell.data.LanguageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModConfigScreen {
    
    // Renk kodları açıklaması (yardım metni için)
    private static final String COLOR_HELP_TEXT = "Use color name (e.g. red, black) or code (e.g. §c, §0).\n" +
            "Available: §0§0 Black, §1§1 Dark Blue, §2§2 Dark Green, §3§3 Dark Aqua,\n" +
            "§4§4 Dark Red, §5§5 Dark Purple, §6§6 Gold, §7§7 Gray,\n" +
            "§8§8 Dark Gray, §9§9 Blue, §a§a Green, §b§b Aqua,\n" +
            "§c§c Red, §d§d Light Purple, §e§e Yellow, §f§f White";

    public static Screen create(Screen parent) {
        // ConfigManager'ı ilk çalıştırmada başlat
        ConfigManager.initialize();
        ModConfig cfg = ConfigManager.getConfig();
        LanguageManager.detectAndSetLanguage();

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Minecraft2Shell"))
                .save(() -> {
                    // Renk isimlerini kodlarına çevir (ConfigManager kullan)
                    ConfigManager.saveCurrentConfigAsTheme(cfg.currentTheme);
                    ConfigManager.saveAll();
                });

        // Genel Ayarlar Kategorisi
        builder.category(ConfigCategory.createBuilder()
                .name(Component.literal(LanguageManager.get("config.general")))

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal(LanguageManager.get("config.show_output")))
                        .description(OptionDescription.of(
                                Component.literal(LanguageManager.get("config.show_output.desc"))))
                        .binding(true, () -> cfg.showOutput, v -> cfg.showOutput = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(Component.literal(LanguageManager.get("config.history_limit")))
                        .description(OptionDescription.of(
                                Component.literal(LanguageManager.get("config.history_limit.desc"))))
                        .binding(50, () -> cfg.historyLimit, v -> cfg.historyLimit = v)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(1, 200)
                                .step(1))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(Component.literal(LanguageManager.get("config.output_limit")))
                        .description(OptionDescription.of(
                                Component.literal(LanguageManager.get("config.output_limit.desc"))))
                        .binding(20, () -> cfg.outputLineLimit, v -> cfg.outputLineLimit = v)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(1, 1000)
                                .step(1))
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal(LanguageManager.get("config.output_reverse")))
                        .description(OptionDescription.of(
                                Component.literal(LanguageManager.get("config.output_reverse.desc"))))
                        .binding(false, () -> cfg.outputReverse, v -> cfg.outputReverse = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal(LanguageManager.get("config.auto_register_aliases")))
                        .description(OptionDescription.of(
                                Component.literal(LanguageManager.get("config.auto_register_aliases.desc"))))
                        .binding(true, () -> cfg.autoRegisterAliases, v -> cfg.autoRegisterAliases = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal(LanguageManager.get("config.confirm_command")))
                        .description(OptionDescription.of(
                                Component.literal(LanguageManager.get("config.confirm_command.desc"))))
                        .binding(true, () -> cfg.confirmCommand, v -> cfg.confirmCommand = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                
                // Windows Shell Seçimi
                .option(Option.<String>createBuilder()
                        .name(Component.literal("Windows Shell"))
                        .description(OptionDescription.of(
                                Component.literal("Choose which shell to use on Windows (cmd or powershell)")))
                        .binding("cmd", () -> cfg.windowsShell, v -> cfg.windowsShell = v)
                        .controller(StringControllerBuilder::create)
                        .build())

                .build());

        // Tema Kategorisi
        ConfigCategory.Builder themeCategory = ConfigCategory.createBuilder()
                .name(Component.literal(LanguageManager.get("config.themes")));
        
        Map<String, ConfigManager.Theme> themesMap = ConfigManager.getThemes();
        if (!themesMap.containsKey(cfg.currentTheme)) {
            cfg.currentTheme = "default";
        }
        
        // Mevcut temayı yükle (eğer değiştiyse)
        if (!themesMap.get(cfg.currentTheme).prefix.equals(cfg.customPrefix)) {
            ConfigManager.applyThemeToConfig();
        }
        
        // Tema Seçimi (metin girişi - güvenilir)
        String themeList = String.join(", ", themesMap.keySet());
        themeCategory.option(Option.<String>createBuilder()
                .name(Component.literal(LanguageManager.get("config.select_theme")))
                .description(OptionDescription.of(
                        Component.literal(LanguageManager.get("config.select_theme.desc") + "\nAvailable: " + themeList)))
                .binding("default", 
                        () -> cfg.currentTheme, 
                        v -> {
                            if (!v.equals(cfg.currentTheme) && themesMap.containsKey(v)) {
                                cfg.currentTheme = v;
                                ConfigManager.applyThemeToConfig();
                            }
                        })
                .controller(StringControllerBuilder::create)
                .build());
        
        // Tema Kaydetme
        themeCategory.option(Option.<String>createBuilder()
                .name(Component.literal("Save Current Theme"))
                .description(OptionDescription.of(
                        Component.literal("Enter a name to save current theme settings")))
                .binding("", 
                        () -> "", 
                        v -> {
                            if (!v.isEmpty()) {
                                ConfigManager.saveCurrentConfigAsTheme(v);
                                cfg.currentTheme = v;
                            }
                        })
                .controller(StringControllerBuilder::create)
                .build());
        
        // Özelleştirilebilir Tema Alanları
        themeCategory.group(OptionGroup.createBuilder()
                .name(Component.literal("Customize Theme"))
                .collapsed(false)
                .option(Option.<String>createBuilder()
                        .name(Component.literal("Prefix"))
                        .description(OptionDescription.of(Component.literal("Chat prefix with color codes. " + COLOR_HELP_TEXT)))
                        .binding("§f[m2s]", () -> cfg.customPrefix, v -> cfg.customPrefix = v)
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Component.literal("Info Color"))
                        .description(OptionDescription.of(Component.literal("Color for info messages. " + COLOR_HELP_TEXT)))
                        .binding("§e", () -> cfg.customInfo, v -> cfg.customInfo = v)
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Component.literal("Success Color"))
                        .description(OptionDescription.of(Component.literal("Color for success messages. " + COLOR_HELP_TEXT)))
                        .binding("§a", () -> cfg.customSuccess, v -> cfg.customSuccess = v)
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Component.literal("Error Color"))
                        .description(OptionDescription.of(Component.literal("Color for error messages. " + COLOR_HELP_TEXT)))
                        .binding("§c", () -> cfg.customError, v -> cfg.customError = v)
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Component.literal("Output Color"))
                        .description(OptionDescription.of(Component.literal("Color for command output. " + COLOR_HELP_TEXT)))
                        .binding("§7", () -> cfg.customOutput, v -> cfg.customOutput = v)
                        .controller(StringControllerBuilder::create)
                        .build())
                .build());

        builder.category(themeCategory.build());

        // Aliaslar Kategorisi
        ConfigCategory.Builder aliasCategory = ConfigCategory.createBuilder()
                .name(Component.literal(LanguageManager.get("config.aliases")));
        
        Map<String, String> aliasesMap = ConfigManager.getAliases();
        boolean hasAliasOptions = false;
        if (!aliasesMap.isEmpty()) {
            for (Map.Entry<String, String> entry : aliasesMap.entrySet()) {
                aliasCategory.option(Option.<String>createBuilder()
                        .name(Component.literal("/" + entry.getKey()))
                        .description(OptionDescription.of(Component.literal("Command: " + entry.getValue())))
                        .binding(entry.getValue(), () -> entry.getValue(), v -> {})
                        .controller(StringControllerBuilder::create)
                        .available(false)
                        .build());
                hasAliasOptions = true;
            }
        }
        // Sadece opsiyon varsa kategoriyi ekle
        if (hasAliasOptions) {
            builder.category(aliasCategory.build());
        }

        // Kara Liste Kategorisi
        ConfigCategory.Builder blacklistCategory = ConfigCategory.createBuilder()
                .name(Component.literal(LanguageManager.get("config.blacklist")));
        
        List<ConfigManager.BlacklistEntry> blacklistEntries = ConfigManager.getBlacklist();
        boolean hasBlacklistOptions = false;
        if (!blacklistEntries.isEmpty()) {
            for (ConfigManager.BlacklistEntry entry : blacklistEntries) {
                blacklistCategory.option(Option.<String>createBuilder()
                        .name(Component.literal("[" + entry.type + "] " + entry.pattern))
                        .binding(entry.pattern, () -> entry.pattern, v -> {})
                        .controller(StringControllerBuilder::create)
                        .available(false)
                        .build());
                hasBlacklistOptions = true;
            }
        }
        if (hasBlacklistOptions) {
            builder.category(blacklistCategory.build());
        }

        // Geçmiş Kategorisi
        ConfigCategory.Builder historyCategory = ConfigCategory.createBuilder()
                .name(Component.literal(LanguageManager.get("config.history")));
        
        List<ConfigManager.HistoryEntry> historyEntries = ConfigManager.getHistory();
        boolean hasHistoryOptions = false;
        if (!historyEntries.isEmpty()) {
            for (int i = historyEntries.size() - 1; i >= Math.max(0, historyEntries.size() - 50); i--) {
                ConfigManager.HistoryEntry entry = historyEntries.get(i);
                historyCategory.option(Option.<String>createBuilder()
                        .name(Component.literal(entry.timestamp))
                        .description(OptionDescription.of(Component.literal(entry.command)))
                        .binding(entry.command, () -> entry.command, v -> {})
                        .controller(StringControllerBuilder::create)
                        .available(false)
                        .build());
                hasHistoryOptions = true;
            }
        }
        if (hasHistoryOptions) {
            builder.category(historyCategory.build());
        }

        return builder.build().generateScreen(parent);
    }
}
