package me.xpyex.mod.cnusername.fabric.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;

public class VersionHandler {
    private static final String MINECRAFT_VERSION;
    private static final int MAJOR_VERSION_INT;

    // 正则表达式匹配 Minecraft 版本号
    private static final Pattern VERSION_PATTERN =
        Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?.*");

    static {
        int majorVersionInt1;
        // 获取 Minecraft 版本
        MINECRAFT_VERSION = FabricLoader.getInstance().getModContainer("minecraft")
                                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                                .orElse("unknown");

        Matcher matcher = VERSION_PATTERN.matcher(MINECRAFT_VERSION);
        if (matcher.matches() && matcher.groupCount() >= 2) {
            try {
                majorVersionInt1 = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                majorVersionInt1 = -1;
            }
        } else {
            majorVersionInt1 = -1;
        }
        MAJOR_VERSION_INT = majorVersionInt1;
    }

    public static String getMinecraftVersion() {
        return MINECRAFT_VERSION;
    }

    public static String getCurrentMixin() {
        return "CnUsername.mixins.v".toLowerCase() + getMajorVersionInt() + ".json";
    }

    /**
     * 获取主版本号作为整数 (如 18, 20)
     */
    public static int getMajorVersionInt() {
        return MAJOR_VERSION_INT;
    }
}
