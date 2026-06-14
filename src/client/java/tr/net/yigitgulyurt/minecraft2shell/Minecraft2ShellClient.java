package tr.net.yigitgulyurt.minecraft2shell;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import tr.net.yigitgulyurt.minecraft2shell.config.ConfigManager;
import tr.net.yigitgulyurt.minecraft2shell.config.ModConfigScreen;
import tr.net.yigitgulyurt.minecraft2shell.data.HistoryManager;
import tr.net.yigitgulyurt.minecraft2shell.data.LanguageManager;

public class Minecraft2ShellClient implements ClientModInitializer, ModMenuApi {

    @Override
    public void onInitializeClient() {
        // ConfigManager'ı başlat
        ConfigManager.initialize();
        
        // HistoryManager'ı başlat
        HistoryManager.initialize();
        
        // Dili ilk başta algıla ve ayarla
        LanguageManager.detectAndSetLanguage();
        
        // ConfigOpener'ı ayarla
        Minecraft2Shell.configOpener = () ->
            Minecraft.getInstance().execute(() ->
                Minecraft.getInstance().setScreen(
                    ModConfigScreen.create(
                        Minecraft.getInstance().screen)));
        
        // Client-side komutları kaydet
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ShellCommand.register(dispatcher);
            ShellCommand.registerAliases(dispatcher);
        });
        
        // Her tick'te dilin değişip değişmediğini kontrol et
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LanguageManager.detectAndSetLanguage();
        });
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModConfigScreen::create;
    }
}
