package tr.net.yigitgulyurt.minecraft2shell;

/**
 * Client tarafindan implement edilir, main'den cagirilir.
 * Bu sayede main source set'inde Minecraft client siniflarina bagimlilik olmaz.
 */
public interface ConfigOpener {
    void open();
}
