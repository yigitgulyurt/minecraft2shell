package tr.net.yigitgulyurt.minecraft2shell.data;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final List<String> history = new ArrayList<>();

    public static void add(String command) {
        history.add(command);
        // Config'teki historyLimit kadar tut
        if (history.size() > tr.net.yigitgulyurt.minecraft2shell.config.ModConfig.get().historyLimit) {
            history.remove(0);
        }
    }

    public static List<String> getAll() {
        return new ArrayList<>(history);
    }

    public static void clear() {
        history.clear();
    }
}