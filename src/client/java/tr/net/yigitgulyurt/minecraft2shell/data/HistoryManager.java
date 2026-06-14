package tr.net.yigitgulyurt.minecraft2shell.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import tr.net.yigitgulyurt.minecraft2shell.config.ConfigManager;

public class HistoryManager {
    
    public static class HistoryEntry {
        public final String command;
        public final LocalDateTime timestamp;
        
        public HistoryEntry(String command) {
            this.command = command;
            this.timestamp = LocalDateTime.now();
        }
        
        public HistoryEntry(String command, LocalDateTime timestamp) {
            this.command = command;
            this.timestamp = timestamp;
        }
        
        public String getFormattedTimestamp() {
            return timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        }
    }
    
    private static final List<HistoryEntry> history = new ArrayList<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static void initialize() {
        // ConfigManager'dan history'yi yükle
        history.clear();
        for (ConfigManager.HistoryEntry entry : ConfigManager.getHistory()) {
            try {
                history.add(new HistoryEntry(
                    entry.command,
                    LocalDateTime.parse(entry.timestamp, FORMATTER)
                ));
            } catch (Exception e) {
                // Geçersiz timestamp, atla
            }
        }
    }

    public static void add(String command) {
        history.add(new HistoryEntry(command));
        // Config'teki historyLimit kadar tut
        if (history.size() > ConfigManager.getConfig().historyLimit) {
            history.remove(0);
        }
        // ConfigManager'a da kaydet
        ConfigManager.addHistory(command, LocalDateTime.now().format(FORMATTER));
    }

    public static List<HistoryEntry> getAll() {
        return new ArrayList<>(history);
    }
    
    public static List<HistoryEntry> search(String query) {
        String q = query.toLowerCase();
        return history.stream()
                .filter(entry -> entry.command.toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public static void clear() {
        history.clear();
        ConfigManager.clearHistory();
    }
}
