package tr.net.yigitgulyurt.minecraft2shell;

public class Minecraft2Shell {
    public static final String MOD_ID = "minecraft2shell";
    public static ConfigOpener configOpener = null;

    public interface ConfigOpener {
        void open();
    }
}