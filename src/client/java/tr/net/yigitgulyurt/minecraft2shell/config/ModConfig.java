package tr.net.yigitgulyurt.minecraft2shell.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import tr.net.yigitgulyurt.minecraft2shell.data.LanguageManager;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("minecraft2shell.json");

    private static ModConfig INSTANCE = new ModConfig();

    // --- Config alanları ---
    public boolean showOutput = true;
    public int historyLimit = 50;
    public int outputLineLimit = 20;
    public boolean outputReverse = false;
    public boolean autoRegisterAliases = true;
    public List<String> blacklist = new ArrayList<>();
    public Map<String, String> aliases = new LinkedHashMap<>();
    
    // Yeni eklenenler
    public boolean confirmCommand = true;
    public boolean useRegexInBlacklist = false; // false = wildcard, true = regex

    // --- Singleton ---
    public static ModConfig get() {
        return INSTANCE;
    }

    // --- Yükle ---
    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            INSTANCE = GSON.fromJson(reader, ModConfig.class);
            if (INSTANCE == null) INSTANCE = new ModConfig();
            if (INSTANCE.blacklist == null) INSTANCE.blacklist = new ArrayList<>();
            if (INSTANCE.aliases == null) INSTANCE.aliases = new LinkedHashMap<>();
            // Dil ayarını artık otomatik algıla
            LanguageManager.detectAndSetLanguage();
        } catch (IOException e) {
            System.err.println("[minecraft2shell] Config yuklenemedi: " + e.getMessage());
            INSTANCE = new ModConfig();
        }
    }

    // --- Kaydet ---
    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            System.err.println("[minecraft2shell] Config kaydedilemedi: " + e.getMessage());
        }
    }

    // --- Kara liste kontrolü ---
    public boolean isBlacklisted(String command) {
        String cmd = command.trim().toLowerCase();
        for (String entry : blacklist) {
            String e = entry.trim().toLowerCase();
            if (e.isEmpty()) continue;
            
            if (useRegexInBlacklist) {
                // Regex modu
                try {
                    if (cmd.matches(e)) {
                        return true;
                    }
                } catch (Exception ignored) {}
            } else {
                // Wildcard modu (simple: * = any chars, ? = any single char)
                if (wildcardMatch(cmd, e)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Wildcard eşleştirme yardımcısı
    private boolean wildcardMatch(String text, String pattern) {
        int textIndex = 0;
        int patternIndex = 0;
        int starIndex = -1;
        int matchIndex = 0;
        
        while (textIndex < text.length()) {
            if (patternIndex < pattern.length() 
                && (pattern.charAt(patternIndex) == text.charAt(textIndex) 
                || pattern.charAt(patternIndex) == '?')) {
                textIndex++;
                patternIndex++;
            } else if (patternIndex < pattern.length() 
                && pattern.charAt(patternIndex) == '*') {
                starIndex = patternIndex++;
                matchIndex = textIndex;
            } else if (starIndex != -1) {
                patternIndex = starIndex + 1;
                textIndex = ++matchIndex;
            } else {
                return false;
            }
        }
        
        while (patternIndex < pattern.length() && pattern.charAt(patternIndex) == '*') {
            patternIndex++;
        }
        
        return patternIndex == pattern.length();
    }
}