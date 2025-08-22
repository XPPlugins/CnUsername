package me.xpyex.module.cnusername.pass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import me.xpyex.module.cnusername.modify.bungee.ClassVisitorAllowedCharacters;
import me.xpyex.module.cnusername.modify.minecraft.ClassVisitorLoginListener;
import me.xpyex.module.cnusername.modify.minecraft.ClassVisitorPacketDataSerializer;
import me.xpyex.module.cnusername.modify.minecraft.ClassVisitorStringUtil;
import me.xpyex.module.cnusername.modify.minecraft.ClassVisitorUtilColor;
import me.xpyex.module.cnusername.modify.mojang.ClassVisitorStringReader;
import me.xpyex.module.cnusername.modify.paper.ClassVisitorCraftPlayerProfile;

public class PassRegistry {
    private static final Map<String, PassEntry> passMap = new LinkedHashMap<>();

    static {
        // bungee
        register(ClassVisitorAllowedCharacters.CLASS_PATH, ClassVisitorAllowedCharacters::new);

        // minecraft
        register(ClassVisitorLoginListener.CLASS_PATH_SPIGOT, ClassVisitorLoginListener::new);
        register(ClassVisitorLoginListener.CLASS_PATH_MOJANG, ClassVisitorLoginListener::new);
        register(ClassVisitorLoginListener.CLASS_PATH_YARN, ClassVisitorLoginListener::new);
        register(ClassVisitorStringUtil.CLASS_PATH, ClassVisitorStringUtil::new);
        register(ClassVisitorUtilColor.CLASS_PATH, ClassVisitorUtilColor::new);
        register(ClassVisitorPacketDataSerializer.CLASS_PATH, ((className, classVisitor, pattern) -> new ClassVisitorPacketDataSerializer(className, classVisitor)));

        // mojang
        register(ClassVisitorStringReader.CLASS_PATH, (className, classVisitor, pattern) -> new ClassVisitorStringReader(className, classVisitor));

        // paper
        register(ClassVisitorCraftPlayerProfile.CLASS_PATH, ClassVisitorCraftPlayerProfile::new);
    }

    private static void register(String className, Pass pass) {
        passMap.put(className, new PassEntry(pass));
    }

    public static Set<String> allPossibleClasses() {
        return passMap.keySet();
    }

    public static Pass getPass(String className) {
        PassEntry entry = passMap.get(className);
        if (entry == null) {
            return null;
        }
        return entry.pass;
    }

    public static boolean isModified(String className) {
        PassEntry entry = passMap.get(className);
        return entry != null && entry.modified;
    }

    public static void setModified(String className) {
        PassEntry entry = passMap.get(className);
        if (entry != null) {
            entry.modified = true;
        }
    }

    private static final class PassEntry {
        private final Pass pass;
        private boolean modified = false;

        private PassEntry(Pass pass) {
            this.pass = pass;
        }
    }
}
