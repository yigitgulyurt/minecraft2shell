package tr.net.yigitgulyurt.minecraft2shell;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfigScreen;

public class Minecraft2ShellClient implements ClientModInitializer, ModMenuApi {

    @Override
    public void onInitializeClient() {
        Minecraft2Shell.configOpener = () ->
            net.minecraft.client.Minecraft.getInstance().execute(() ->
                net.minecraft.client.Minecraft.getInstance().setScreen(
                    ModConfigScreen.create(
                        net.minecraft.client.Minecraft.getInstance().screen)));
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModConfigScreen::create;
    }
}
