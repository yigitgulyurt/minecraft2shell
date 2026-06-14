package tr.net.yigitgulyurt.minecraft2shell;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfig;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfigScreen;

public class Minecraft2ShellClient implements ClientModInitializer, ModMenuApi {

    @Override
    public void onInitializeClient() {
        // Config'i yükle (bu aynı zamanda dili de algılar)
        ModConfig.load();
        
        // ConfigOpener'ı ayarla
        Minecraft2Shell.configOpener = () ->
            net.minecraft.client.Minecraft.getInstance().execute(() ->
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    ModConfigScreen.create(
                        net.minecraft.client.Minecraft.getInstance().screen)));
        
        // Client-side komutları kaydet
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ShellCommand.register(dispatcher);
            ShellCommand.registerAliases(dispatcher);
        });
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModConfigScreen::create;
    }
}
