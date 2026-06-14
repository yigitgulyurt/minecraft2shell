package tr.net.yigitgulyurt.minecraft2shell.data;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static final Map<String, Map<String, String>> translations = new HashMap<>();
    private static String currentLanguage = "tr"; // Default: Türkçe

    static {
        // Türkçe çeviriler
        Map<String, String> tr = new HashMap<>();
        tr.put("command.history.title", "§6[m2s] Komut geçmişi:");
        tr.put("command.history.empty", "§e[m2s] Geçmiş boş.");
        tr.put("command.history.cleared", "§a[m2s] Geçmiş temizlendi.");
        tr.put("command.history.invalid_index", "§c[m2s] Geçersiz index: ");
        tr.put("command.history.executing", "§6[m2s] Çalıştırılıyor: ");
        tr.put("command.alias.title", "§6[m2s] Alias listesi:");
        tr.put("command.alias.none", "§e[m2s] Hiç alias yok.");
        tr.put("command.alias.added", "§a[m2s] Alias eklendi: /");
        tr.put("command.alias.needs_restart", "§e[m2s] Alias'ın aktif olması için oyunu yeniden başlat.");
        tr.put("command.alias.removed", "§a[m2s] Alias silindi: ");
        tr.put("command.alias.not_found", "§c[m2s] Alias bulunamadı: ");
        tr.put("command.blacklist.title", "§6[m2s] Kara liste:");
        tr.put("command.blacklist.empty", "§e[m2s] Kara liste boş.");
        tr.put("command.blacklist.added", "§a[m2s] Kara listeye eklendi: ");
        tr.put("command.blacklist.removed", "§a[m2s] Kara listeden çıkarıldı: ");
        tr.put("command.blacklist.not_found", "§c[m2s] Kara listede bulunamadı: ");
        tr.put("command.blacklist.error", "§c[m2s] Bu komut kara listede: ");
        tr.put("command.config.not_supported", "§c[m2s] Config ekranı bu ortamda desteklenmiyor.");
        tr.put("command.output.remaining", "§e[m2s] ... ve %d satır daha (limit: %d). Limiti /shell config'den artirabilirsin.");
        tr.put("command.output.success", "§a[m2s] Tamamlandı (kod: %d)");
        tr.put("command.error", "§c[m2s] Hata: ");
        tr.put("config.general", "Genel");
        tr.put("config.show_output", "Çıktı Göster");
        tr.put("config.show_output_desc", "Komut çıktısını chat'te göster");
        tr.put("config.history_limit", "Geçmiş Limiti");
        tr.put("config.history_limit_desc", "Kaç komut geçmişi tutulsun");
        tr.put("config.output_limit", "Çıktı Satır Limiti");
        tr.put("config.output_limit_desc", "Uzun çıktılarda en fazla kaç satır gösterilsin");
        tr.put("config.output_reverse", "Sondan Göster");
        tr.put("config.output_reverse_desc", "Çıktıyı baştan değil, sondan göster");
        tr.put("config.auto_register_aliases", "Aliasları Otomatik Kaydet");
        tr.put("config.auto_register_aliases_desc", "Aliasları ayrı komut olarak kaydet (oyunu yeniden başlattıktan sonra aktif olur)");
        translations.put("tr", tr);

        // English translations
        Map<String, String> en = new HashMap<>();
        en.put("command.history.title", "§6[m2s] Command history:");
        en.put("command.history.empty", "§e[m2s] History is empty.");
        en.put("command.history.cleared", "§a[m2s] History cleared.");
        en.put("command.history.invalid_index", "§c[m2s] Invalid index: ");
        en.put("command.history.executing", "§6[m2s] Executing: ");
        en.put("command.alias.title", "§6[m2s] Alias list:");
        en.put("command.alias.none", "§e[m2s] No aliases found.");
        en.put("command.alias.added", "§a[m2s] Alias added: /");
        en.put("command.alias.needs_restart", "§e[m2s] Restart the game for the alias to take effect.");
        en.put("command.alias.removed", "§a[m2s] Alias removed: ");
        en.put("command.alias.not_found", "§c[m2s] Alias not found: ");
        en.put("command.blacklist.title", "§6[m2s] Blacklist:");
        en.put("command.blacklist.empty", "§e[m2s] Blacklist is empty.");
        en.put("command.blacklist.added", "§a[m2s] Added to blacklist: ");
        en.put("command.blacklist.removed", "§a[m2s] Removed from blacklist: ");
        en.put("command.blacklist.not_found", "§c[m2s] Not found in blacklist: ");
        en.put("command.blacklist.error", "§c[m2s] This command is blacklisted: ");
        en.put("command.config.not_supported", "§c[m2s] Config screen is not supported in this environment.");
        en.put("command.output.remaining", "§e[m2s] ... and %d more lines (limit: %d). You can increase the limit via /shell config.");
        en.put("command.output.success", "§a[m2s] Completed (code: %d)");
        en.put("command.error", "§c[m2s] Error: ");
        en.put("config.general", "General");
        en.put("config.show_output", "Show Output");
        en.put("config.show_output_desc", "Show command output in chat");
        en.put("config.history_limit", "History Limit");
        en.put("config.history_limit_desc", "How many commands to keep in history");
        en.put("config.output_limit", "Output Line Limit");
        en.put("config.output_limit_desc", "Max number of lines to show for long outputs");
        en.put("config.output_reverse", "Show From End");
        en.put("config.output_reverse_desc", "Show output from the end instead of the beginning");
        en.put("config.auto_register_aliases", "Auto Register Aliases");
        en.put("config.auto_register_aliases_desc", "Register aliases as separate commands (takes effect after restarting the game)");
        translations.put("en", en);
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
