package tr.net.yigitgulyurt.minecraft2shell;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfig;

public class Minecraft2Shell implements ModInitializer {

    public static final String MOD_ID = "minecraft2shell";
    public static ConfigOpener configOpener = null;

    @Override
    public void onInitialize() {
        ModConfig.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ShellCommand.register(dispatcher);
            ShellCommand.registerAliases(dispatcher);
        });
    }
}
