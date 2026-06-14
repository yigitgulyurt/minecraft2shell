package tr.net.yigitgulyurt.minecraft2shell.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import tr.net.yigitgulyurt.minecraft2shell.data.LanguageManager;

public class ModConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig cfg = ModConfig.get();
        // Config ekranını açarken dili tekrar algıla (eğer oyuncu oyunda dili değiştirmişse)
        LanguageManager.detectAndSetLanguage();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Minecraft2Shell"))
                .category(ConfigCategory.createBuilder()
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

                        .build())
                .save(ModConfig::save)
                .build()
                .generateScreen(parent);
    }
}
