package tr.net.yigitgulyurt.minecraft2shell.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.*;
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
        public String type; // "specific", "wildcard", "regex"
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
        // Herhangi bir dosya okunamazsa veya formatı bozuksa varsayılanı kullan
        try {
            config = loadJson(CONFIG_FILE, ModConfig.class, new ModConfig());
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Config yüklenemedi, varsayılan kullanılıyor: " + e.getMessage());
            config = new ModConfig();
            saveJson(CONFIG_FILE, config);
        }

        try {
            blacklist = loadBlacklist();
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Blacklist yüklenemedi, temizleniyor: " + e.getMessage());
            blacklist = new ArrayList<>();
            saveBlacklist();
        }

        try {
            aliases = loadAliases();
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Aliases yüklenemedi, temizleniyor: " + e.getMessage());
            aliases = new LinkedHashMap<>();
            saveAliases();
        }

        try {
            history = loadHistory();
        } catch (Exception e) {
            System.err.println("[minecraft2shell] History yüklenemedi, temizleniyor: " + e.getMessage());
            history = new ArrayList<>();
            saveHistory();
        }

        try {
            themes = loadThemes();
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Temalar yüklenemedi, temizleniyor: " + e.getMessage());
            themes = new LinkedHashMap<>();
            loadDefaultThemes();
        }
    }
    
    public static void saveAll() {
        saveJson(CONFIG_FILE, config);
        saveBlacklist();
        saveAliases();
        saveHistory();
        saveThemes();
    }
    
    private static List<BlacklistEntry> loadBlacklist() {
        if (!Files.exists(BLACKLIST_FILE)) {
            saveBlacklist();
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(BLACKLIST_FILE)) {
            TypeToken<List<BlacklistEntry>> type = new TypeToken<List<BlacklistEntry>>() {};
            List<BlacklistEntry> list = GSON.fromJson(reader, type.getType());
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Blacklist okunurken hata: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private static void saveBlacklist() {
        saveJson(BLACKLIST_FILE, blacklist);
    }
    
    private static Map<String, String> loadAliases() {
        if (!Files.exists(ALIASES_FILE)) {
            saveAliases();
            return new LinkedHashMap<>();
        }
        try (Reader reader = Files.newBufferedReader(ALIASES_FILE)) {
            TypeToken<Map<String, String>> type = new TypeToken<Map<String, String>>() {};
            Map<String, String> map = GSON.fromJson(reader, type.getType());
            return map != null ? map : new LinkedHashMap<>();
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Aliases okunurken hata: " + e.getMessage());
            return new LinkedHashMap<>();
        }
    }
    
    private static void saveAliases() {
        saveJson(ALIASES_FILE, aliases);
    }
    
    private static List<HistoryEntry> loadHistory() {
        if (!Files.exists(HISTORY_FILE)) {
            saveHistory();
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(HISTORY_FILE)) {
            TypeToken<List<HistoryEntry>> type = new TypeToken<List<HistoryEntry>>() {};
            List<HistoryEntry> list = GSON.fromJson(reader, type.getType());
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[minecraft2shell] History okunurken hata: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private static void saveHistory() {
        saveJson(HISTORY_FILE, history);
    }
    
    private static Map<String, Theme> loadThemes() {
        if (!Files.exists(THEMES_FILE)) {
            loadDefaultThemes();
            return themes;
        }
        try (Reader reader = Files.newBufferedReader(THEMES_FILE)) {
            TypeToken<Map<String, Theme>> type = new TypeToken<Map<String, Theme>>() {};
            Map<String, Theme> map = GSON.fromJson(reader, type.getType());
            return map != null ? map : new LinkedHashMap<>();
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Temalar okunurken hata: " + e.getMessage());
            return new LinkedHashMap<>();
        }
    }
    
    public static void saveThemes() {
        saveJson(THEMES_FILE, themes);
    }
    
    // --- Getters and Setters ---
    
    public static ModConfig getConfig() { return config; }
    
    public static List<BlacklistEntry> getBlacklist() { return blacklist; }
    public static void addToBlacklist(String type, String pattern) {
        blacklist.add(new BlacklistEntry(type, pattern));
        saveBlacklist();
    }
    public static boolean removeFromBlacklist(String pattern) {
        boolean removed = blacklist.removeIf(e -> e.pattern.equals(pattern));
        saveBlacklist();
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
        saveAliases();
    }
    public static void addAlias(String name, String command) {
        aliases.put(name, command);
        saveAliases();
    }
    public static void removeAlias(String name) {
        aliases.remove(name);
        saveAliases();
    }
    
    public static List<HistoryEntry> getHistory() { return history; }
    public static void setHistory(List<HistoryEntry> list) {
        history = list;
        saveHistory();
    }
    public static void addHistory(String command, String timestamp) {
        history.add(new HistoryEntry(command, timestamp));
        if (history.size() > config.historyLimit) {
            history.remove(0);
        }
        saveHistory();
    }
    public static void clearHistory() {
        history.clear();
        saveHistory();
    }
    
    public static Map<String, Theme> getThemes() { return themes; }
    public static Theme getCurrentTheme() {
        String themeName = config.currentTheme;
        return themes.getOrDefault(themeName, themes.get("default"));
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

    private static void saveJson(Path path, Object data) {
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("[minecraft2shell] Dosya kaydedilemedi " + path + ": " + e.getMessage());
        }
    }
    
    // --- Config Import/Export ---
    
    public static boolean exportConfig(Path exportDir) {
        try {
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }
            
            // Tüm dosyaları dışa aktar
            Path[] files = {CONFIG_FILE, BLACKLIST_FILE, ALIASES_FILE, HISTORY_FILE, THEMES_FILE};
            for (Path file : files) {
                if (Files.exists(file)) {
                    Files.copy(file, exportDir.resolve(file.getFileName()));
                }
            }
            
            // Output klasörünü dışa aktar
            if (Files.exists(OUTPUT_DIR)) {
                Path exportOutputDir = exportDir.resolve(OUTPUT_DIR.getFileName());
                if (!Files.exists(exportOutputDir)) {
                    Files.createDirectories(exportOutputDir);
                }
                try (var stream = Files.list(OUTPUT_DIR)) {
                    stream.forEach(source -> {
                        try {
                            Files.copy(source, exportOutputDir.resolve(source.getFileName()));
                        } catch (IOException e) {
                            System.err.println("[minecraft2shell] Output dosyası kopyalanamadı: " + e.getMessage());
                        }
                    });
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Config dışa aktarılamadı: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean importConfig(Path importDir) {
        try {
            if (!Files.exists(importDir)) {
                return false;
            }
            
            // Tüm dosyaları içe aktar
            Path[] files = {CONFIG_FILE, BLACKLIST_FILE, ALIASES_FILE, HISTORY_FILE, THEMES_FILE};
            for (Path file : files) {
                Path sourceFile = importDir.resolve(file.getFileName());
                if (Files.exists(sourceFile)) {
                    Files.copy(sourceFile, file);
                }
            }
            
            // Output klasörünü içe aktar
            Path importOutputDir = importDir.resolve(OUTPUT_DIR.getFileName());
            if (Files.exists(importOutputDir)) {
                try (var stream = Files.list(importOutputDir)) {
                    stream.forEach(source -> {
                        try {
                            Files.copy(source, OUTPUT_DIR.resolve(source.getFileName()));
                        } catch (IOException e) {
                            System.err.println("[minecraft2shell] Output dosyası içe aktarılamadı: " + e.getMessage());
                        }
                    });
                }
            }
            
            // Yeni configleri yükle
            loadAll();
            return true;
        } catch (Exception e) {
            System.err.println("[minecraft2shell] Config içe aktarılamadı: " + e.getMessage());
            return false;
        }
    }
}
