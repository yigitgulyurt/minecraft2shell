package tr.net.yigitgulyurt.minecraft2shell.config;

import java.util.List;

public class ModConfig {

    // --- Config alanları ---
    public boolean showOutput = true;
    public int historyLimit = 50;
    public int outputLineLimit = 20;
    public boolean outputReverse = false;
    public boolean autoRegisterAliases = true;
    public boolean confirmCommand = true;
    public boolean useRegexInBlacklist = false; // false = wildcard, true = regex
    public String currentTheme = "default";
    
    // --- Kara liste kontrolü ---
    public boolean isBlacklisted(String command) {
        List<String> blacklist = ConfigManager.getBlacklist();
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