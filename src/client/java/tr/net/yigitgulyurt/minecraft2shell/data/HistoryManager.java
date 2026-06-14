package tr.net.yigitgulyurt.minecraft2shell.data;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class HistoryManager {
    public static class HistoryEntry {
        public final String command;
        public final LocalDateTime timestamp;
        
        public HistoryEntry(String command) {
            this.command = command;
            this.timestamp = LocalDateTime.now();
        }
        
        public String getFormattedTimestamp() {
            return timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        }
    }
    
    private static final List<HistoryEntry> history = new ArrayList<>();

    public static void add(String command) {
        history.add(new HistoryEntry(command));
        // Config'teki historyLimit kadar tut
        if (history.size() > tr.net.yigitgulyurt.minecraft2shell.config.ModConfig.get().historyLimit) {
            history.remove(0);
        }
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
    }
}
