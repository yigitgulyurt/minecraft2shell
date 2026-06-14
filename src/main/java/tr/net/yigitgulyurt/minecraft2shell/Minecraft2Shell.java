package tr.net.yigitgulyurt.minecraft2shell;

import net.fabricmc.api.ModInitializer;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfig;

public class Minecraft2Shell implements ModInitializer {

    public static final String MOD_ID = "minecraft2shell";
    public static ConfigOpener configOpener = null;

    @Override
    public void onInitialize() {
        ModConfig.load();
    }
}
