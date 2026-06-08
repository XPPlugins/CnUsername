package me.xpyex.mod.cnusername.fabric.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;

public class VersionHandler {
    private static final String MINECRAFT_VERSION;
    private static final int MAJOR_VERSION_INT;

    static {
        // 获取 Minecraft 版本
        MINECRAFT_VERSION = FabricLoader.getInstance().getModContainer("minecraft")
                                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                                .orElse("unknown");

        var array = MINECRAFT_VERSION.split("\\.");
        MAJOR_VERSION_INT = ("1".equals(array[0])) ? Integer.parseInt(array[1]) : Integer.parseInt(array[0]);
    }

    public static String getMinecraftVersion() {
        return MINECRAFT_VERSION;
    }

    public static String getCurrentMixin() {
        int ver = Math.min(getMajorVersionInt(), 26);  // 新版本都用的Mojang官方map，就不再需要向后兼容，只需要Fabric能加载就能跑
        return "CnUsername.mixins.fabric.v".toLowerCase() + ver + ".json";
    }

    /**
     * 获取主版本号作为整数 (如 18, 20)
     */
    public static int getMajorVersionInt() {
        return MAJOR_VERSION_INT;
    }
}
