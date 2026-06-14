package tr.net.yigitgulyurt.minecraft2shell.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static final Gson GSON = new Gson();
    private static final Type TYPE = new TypeToken<Map<String, String>>() {}.getType();
    
    private static final Map<String, Map<String, String>> translations = new HashMap<>();
    private static String currentLanguage = "tr";
    private static String lastDetectedLanguage = null;

    static {
        loadLanguage("tr");
        loadLanguage("en");
    }

    private static void loadLanguage(String lang) {
        try {
            InputStream input = LanguageManager.class.getClassLoader()
                .getResourceAsStream("assets/minecraft2shell/lang/" + lang + ".json");
            if (input != null) {
                InputStreamReader reader = new InputStreamReader(input);
                Map<String, String> langMap = GSON.fromJson(reader, TYPE);
                translations.put(lang, langMap);
                reader.close();
            } else {
                System.err.println("[minecraft2shell] Language file not found: " + lang);
            }
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Error loading language " + lang + ": " + e.getMessage());
        }
    }

    public static void detectAndSetLanguage() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.options != null) {
                String mcLang = mc.options.languageCode;
                String targetLanguage;
                
                if (mcLang != null && mcLang.startsWith("tr")) {
                    targetLanguage = "tr";
                } else {
                    targetLanguage = "en";
                }
                
                // Eğer değişiklik yoksa hiçbir şey yapma
                if (!targetLanguage.equals(lastDetectedLanguage)) {
                    setLanguage(targetLanguage);
                    lastDetectedLanguage = targetLanguage;
                }
            }
        } catch (Exception e) {
            // Hata olursa yine bir kere ayarla
            if (!"tr".equals(lastDetectedLanguage)) {
                setLanguage("tr");
                lastDetectedLanguage = "tr";
            }
        }
    }

    public static void setLanguage(String lang) {
        if (translations.containsKey(lang)) {
            currentLanguage = lang;
        }
    }

    public static String get(String key) {
        return translations.get(currentLanguage).getOrDefault(key, key);
    }

    public static String get(String key, Object... args) {
        return String.format(get(key), args);
    }
}
