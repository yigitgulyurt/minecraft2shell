package tr.net.yigitgulyurt.minecraft2shell.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private static final Path BASE_DIR = FabricLoader.getInstance()
            .getConfigDir().resolve("minecraft2shell");
    
    private static final Path CONFIG_FILE = BASE_DIR.resolve("config.json");
    private static final Path BLACKLIST_FILE = BASE_DIR.resolve("blacklist.json");
    private static final Path ALIASES_FILE = BASE_DIR.resolve("aliases.json");
    private static final Path HISTORY_FILE = BASE_DIR.resolve("history.json");
    private static final Path THEMES_FILE = BASE_DIR.resolve("themes.json");
    private static final Path OUTPUT_DIR = BASE_DIR.resolve("outputs");
    
    private static ModConfig config = new ModConfig();
    private static List<BlacklistEntry> blacklist = new ArrayList<>();
    private static Map<String, String> aliases = new LinkedHashMap<>();
    private static List<HistoryEntry> history = new ArrayList<>();
    private static Map<String, Theme> themes = new LinkedHashMap<>();
    
    public static class HistoryEntry {
        public String command;
        public String timestamp;
        
        public HistoryEntry() {}
        public HistoryEntry(String command, String timestamp) {
            this.command = command;
            this.timestamp = timestamp;
        }
    }
    
    public static class BlacklistEntry {
        public String type; // "wildcard", "specific", "regex"
        public String pattern;
        
        public BlacklistEntry() {}
        public BlacklistEntry(String type, String pattern) {
            this.type = type;
            this.pattern = pattern;
        }
    }
    
    public static class Theme {
        public String prefix = "§6[m2s]";
        public String info = "§e";
        public String success = "§a";
        public String error = "§c";
        public String output = "§7";
        
        public Theme() {}
    }
    
    public static void initialize() {
        try {
            Files.createDirectories(BASE_DIR);
            Files.createDirectories(OUTPUT_DIR);
            loadAll();
            loadDefaultThemes();
        } catch (IOException e) {
            System.err.println("[minecraft2shell] Config klasörü oluşturulamadı: " + e.getMessage());
        }
    }
    
    public static Path getOutputDir() {
        return OUTPUT_DIR;
    }
    
    public static Path getOutputPath(String filename) {
        return OUTPUT_DIR.resolve(filename);
    }
    
    private static void loadDefaultThemes() {
        if (themes.isEmpty()) {
            themes.put("default", new Theme());
            
            Theme dark = new Theme();
            dark.prefix = "§5[m2s]";
            dark.info = "§d";
            dark.success = "§2";
            dark.error = "§4";
            dark.output = "§8";
            themes.put("dark", dark);
            
            Theme light = new Theme();
            light.prefix = "§b[m2s]";
            light.info = "§3";
            light.success = "§a";
            light.error = "§c";
            light.output = "§f";
            themes.put("light", light);
            
            saveThemes();
        }
    }
    
    public static void loadAll() {
        config = loadJson(CONFIG_FILE, ModConfig.class, new ModConfig());
        blacklist = loadJsonList(BLACKLIST_FILE, BlacklistEntry.class, new ArrayList<>());
        aliases = loadJsonMap(ALIASES_FILE, String.class, String.class, new LinkedHashMap<>());
        history = loadJsonList(HISTORY_FILE, HistoryEntry.class, new ArrayList<>());
        themes = loadJsonMap(THEMES_FILE, String.class, Theme.class, new LinkedHashMap<>());
    }
    
    public static void saveAll() {
        saveJson(CONFIG_FILE, config);
        saveJson(BLACKLIST_FILE, blacklist);
        saveJson(ALIASES_FILE, aliases);
        saveJson(HISTORY_FILE, history);
        saveJson(THEMES_FILE, themes);
    }
    
    // --- Getters and Setters ---
    
    public static ModConfig getConfig() { return config; }
    
    public static List<BlacklistEntry> getBlacklist() { return blacklist; }
    public static void addToBlacklist(String type, String pattern) {
        blacklist.add(new BlacklistEntry(type, pattern));
        saveJson(BLACKLIST_FILE, blacklist);
    }
    public static boolean removeFromBlacklist(String pattern) {
        boolean removed = blacklist.removeIf(e -> e.pattern.equals(pattern));
        saveJson(BLACKLIST_FILE, blacklist);
        return removed;
    }
    
    public static boolean isBlacklisted(String command) {
        for (BlacklistEntry entry : blacklist) {
            if (matches(entry, command)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean matches(BlacklistEntry entry, String command) {
        switch (entry.type) {
            case "specific":
                return command.equalsIgnoreCase(entry.pattern);
            case "wildcard":
                return wildcardMatch(command, entry.pattern);
            case "regex":
                try {
                    Pattern pattern = Pattern.compile(entry.pattern, Pattern.CASE_INSENSITIVE);
                    return pattern.matcher(command).find();
                } catch (Exception e) {
                    return false;
                }
            default:
                return false;
        }
    }

    private static boolean wildcardMatch(String text, String pattern) {
        String regex = pattern
                .replace(".", "\\.")
                .replace("?", ".")
                .replace("*", ".*");
        return Pattern.compile("^" + regex + "$", Pattern.CASE_INSENSITIVE).matcher(text).find();
    }
    
    public static Map<String, String> getAliases() { return aliases; }
    public static void setAliases(Map<String, String> map) {
        aliases = map;
        saveJson(ALIASES_FILE, aliases);
    }
    public static void addAlias(String name, String command) {
        aliases.put(name, command);
        saveJson(ALIASES_FILE, aliases);
    }
    public static void removeAlias(String name) {
        aliases.remove(name);
        saveJson(ALIASES_FILE, aliases);
    }
    
    public static List<HistoryEntry> getHistory() { return history; }
    public static void setHistory(List<HistoryEntry> list) {
        history = list;
        saveJson(HISTORY_FILE, history);
    }
    public static void addHistory(String command, String timestamp) {
        history.add(new HistoryEntry(command, timestamp));
        if (history.size() > config.historyLimit) {
            history.remove(0);
        }
        saveJson(HISTORY_FILE, history);
    }
    public static void clearHistory() {
        history.clear();
        saveJson(HISTORY_FILE, history);
    }
    
    public static Map<String, Theme> getThemes() { return themes; }
    public static Theme getCurrentTheme() {
        String themeName = config.currentTheme;
        return themes.getOrDefault(themeName, themes.get("default"));
    }
    public static void saveThemes() {
        saveJson(THEMES_FILE, themes);
    }
    
    // --- JSON Helpers ---
    
    private static <T> T loadJson(Path path, Class<T> clazz, T defaultValue) {
        if (!Files.exists(path)) {
            saveJson(path, defaultValue);
            return defaultValue;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            T result = GSON.fromJson(reader, clazz);
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Dosya yüklenemedi " + path + ": " + e.getMessage());
            return defaultValue;
        }
    }
    
    private static <T> List<T> loadJsonList(Path path, Class<T> clazz, List<T> defaultValue) {
        if (!Files.exists(path)) {
            saveJson(path, defaultValue);
            return defaultValue;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            com.google.gson.reflect.TypeToken<List<T>> token = 
                new com.google.gson.reflect.TypeToken<List<T>>(){};
            List<T> result = GSON.fromJson(reader, token.getType());
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Liste yüklenemedi " + path + ": " + e.getMessage());
            return defaultValue;
        }
    }
    
    private static <K, V> Map<K, V> loadJsonMap(Path path, Class<K> keyClazz, Class<V> valueClazz, Map<K, V> defaultValue) {
        if (!Files.exists(path)) {
            saveJson(path, defaultValue);
            return defaultValue;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            com.google.gson.reflect.TypeToken<Map<K, V>> token = 
                new com.google.gson.reflect.TypeToken<Map<K, V>>(){};
            Map<K, V> result = GSON.fromJson(reader, token.getType());
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Harita yüklenemedi " + path + ": " + e.getMessage());
            return defaultValue;
        }
    }
    
    private static void saveJson(Path path, Object data) {
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("[minecraft2shell] Dosya kaydedilemedi " + path + ": " + e.getMessage());
        }
    }
}
