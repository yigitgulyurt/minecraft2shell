package tr.net.yigitgulyurt.minecraft2shell.config;

public class ModConfig {

    // --- Config alanları ---
    public boolean showOutput = true;
    public int historyLimit = 50;
    public int outputLineLimit = 20;
    public boolean outputReverse = false;
    public boolean autoRegisterAliases = true;
    public boolean confirmCommand = true;
    public String currentTheme = "default";
    // Mevcut temanın özelleştirilebilir alanları
    public String customPrefix = "§f[m2s]";
    public String customInfo = "§e";
    public String customSuccess = "§a";
    public String customError = "§c";
    public String customOutput = "§7";
}
