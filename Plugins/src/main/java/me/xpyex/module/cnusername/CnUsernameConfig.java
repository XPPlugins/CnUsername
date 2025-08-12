package me.xpyex.module.cnusername;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class CnUsernameConfig {
    public static final String DEFAULT_PATTERN = "^[a-zA-Z0-9_]{3,16}|[a-zA-Z0-9_\u4e00-\u9fa5]{2,10}|CS\\-CoreLib$";
    public static File folder = null;
    private static boolean debug;
    private static String pattern = null;

    public static File getFolder() {
        return folder;
    }

    public static void setFolder(File folder) {
        try {
            if (folder.exists() && folder.isFile()) {
                throw new IllegalStateException("错误: 已存在CnUsername文件，且非文件夹: " + folder.getAbsolutePath());
            }
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        CnUsernameConfig.folder = folder;
        Logging.info("CnUsername的文件将会存放在: " + folder.getAbsolutePath());
    }

    public static String getPattern() {
        if (pattern == null) loadConfig();
        return pattern;
    }

    public static void loadConfig() {
        if (getFolder() == null) setFolder(new File("CnUsername"));
        try {
            File debugFile = new File(getFolder(), "debug.txt");
            if (!debugFile.exists()) {
                Files.write(debugFile.toPath(), "false".getBytes(StandardCharsets.UTF_8));
            }
            debug = "true".equalsIgnoreCase(Files.readAllLines(debugFile.toPath(), StandardCharsets.UTF_8).get(0));
        } catch (Exception e) {
            debug = false;
            e.printStackTrace();
        }
        if (isDebug()) {
            Logging.info("当前Debug已启用，修改类时将会保存样本");
        }


        try {
            File patternFile = new File(folder, "pattern.txt");
            if (!patternFile.exists()) {
                Files.write(patternFile.toPath(), DEFAULT_PATTERN.getBytes(StandardCharsets.UTF_8));
            }
            pattern = Files.readAllLines(patternFile.toPath(), StandardCharsets.UTF_8).get(0);
            if (pattern.trim().isEmpty()) {
                pattern = DEFAULT_PATTERN;
            }
        } catch (Exception e) {
            pattern = DEFAULT_PATTERN;
            e.printStackTrace();
        }
        Logging.info("当前使用的正则为: " + pattern);
    }

    public static boolean isDebug() {
        return debug;
    }
}
