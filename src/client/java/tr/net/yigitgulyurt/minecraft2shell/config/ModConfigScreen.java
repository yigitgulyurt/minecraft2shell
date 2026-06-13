package tr.net.yigitgulyurt.minecraft2shell.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;

public class ModConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig cfg = ModConfig.get();

        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Minecraft2Shell"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Genel"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Çıktı Göster"))
                                .description(OptionDescription.of(
                                        Component.literal("Komut çıktısını chat'te göster")))
                                .binding(true, () -> cfg.showOutput, v -> cfg.showOutput = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(Component.literal("Geçmiş Limiti"))
                                .description(OptionDescription.of(
                                        Component.literal("Kaç komut geçmişi tutulsun (1-200)")))
                                .binding(50, () -> cfg.historyLimit, v -> cfg.historyLimit = v)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(1, 200)
                                        .step(1))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(Component.literal("Çıktı Satır Limiti"))
                                .description(OptionDescription.of(
                                        Component.literal("Uzun çıktılarda en fazla kaç satır gösterilsin (1-100)")))
                                .binding(20, () -> cfg.outputLineLimit, v -> cfg.outputLineLimit = v)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(1, 100)
                                        .step(1))
                                .build())

                        .build())
                .save(ModConfig::save)
                .build()
                .generateScreen(parent);
    }
}
