package tr.net.yigitgulyurt.minecraft2shell.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

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
    public List<String> blacklist = new ArrayList<>();
    public Map<String, String> aliases = new LinkedHashMap<>();

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

    // --- Kara liste kontrolu (prefix match) ---
    public boolean isBlacklisted(String command) {
        String cmd = command.trim().toLowerCase();
        for (String entry : blacklist) {
            String e = entry.trim().toLowerCase();
            if (cmd.equals(e) || cmd.startsWith(e + " ")) {
                return true;
            }
        }
        return false;
    }
}
