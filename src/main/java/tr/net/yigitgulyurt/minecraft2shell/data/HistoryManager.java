package tr.net.yigitgulyurt.minecraft2shell.data;

import tr.net.yigitgulyurt.minecraft2shell.config.ModConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryManager {

    private static final List<String> history = new ArrayList<>();

    public static void add(String command) {
        // Ayni komutu arka arkaya ekleme
        if (!history.isEmpty() && history.get(history.size() - 1).equals(command)) return;

        history.add(command);

        // Limit asildiysa en eskiyi sil
        int limit = ModConfig.get().historyLimit;
        while (history.size() > limit) {
            history.remove(0);
        }
    }

    public static List<String> getAll() {
        return Collections.unmodifiableList(history);
    }

    public static void clear() {
        history.clear();
    }
}
