package tr.net.yigitgulyurt.minecraft2shell.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import tr.net.yigitgulyurt.minecraft2shell.data.HistoryManager;
import tr.net.yigitgulyurt.minecraft2shell.data.LanguageManager;

import java.util.ArrayList;
import java.util.List;

public class ModConfigScreen {

    public static Screen create(Screen parent) {
        // ConfigManager'ı ilk çalıştırmada başlat
        ConfigManager.initialize();
        ModConfig cfg = ConfigManager.getConfig();
        LanguageManager.detectAndSetLanguage();

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Minecraft2Shell"))
                .save(ConfigManager::saveAll);

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

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal(LanguageManager.get("config.use_regex_blacklist")))
                        .description(OptionDescription.of(
                                Component.literal(LanguageManager.get("config.use_regex_blacklist.desc"))))
                        .binding(false, () -> cfg.useRegexInBlacklist, v -> cfg.useRegexInBlacklist = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .build());

        // Tema Kategorisi - şimdilik devre dışı
        /*
        ConfigCategory.Builder themeCategory = ConfigCategory.createBuilder()
                .name(Component.literal("Themes"));

        // Tema Seçimi - basitleştirilmiş
        List<String> themeNames = new ArrayList<>(ConfigManager.getThemes().keySet());
        themeCategory.option(Option.<String>createBuilder()
                .name(Component.literal("Select Theme"))
                .description(OptionDescription.of(
                        Component.literal("Choose which theme to use")))
                .binding("default", () -> cfg.currentTheme, v -> cfg.currentTheme = v)
                .controller(opt -> StringControllerBuilder.create(opt))
                .build());

        builder.category(themeCategory.build());
        */

        // History, Alias ve Blacklist kategorileri - şimdilik basit

        return builder.build().generateScreen(parent);
    }
}
