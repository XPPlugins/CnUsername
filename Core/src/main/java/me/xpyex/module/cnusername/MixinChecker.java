package me.xpyex.module.cnusername;

/**
 * Mixin 辅助工具类，提供复用的逻辑
 */
public class MixinChecker {
    public static Boolean checkPlayerName(String name) {
        if (name.trim().isEmpty()) {
            return false;
        } else if (name.matches(CnUsernameConfig.getPattern())) {
            return true;
        }
        return null;  // null在mixin中不修改
    }
}
