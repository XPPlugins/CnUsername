package me.xpyex.module.cnusername;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class CnUsernameConfig {
    public static final String DEFAULT_PATTERN = "^[a-zA-Z0-9_]{3,16}|[a-zA-Z0-9_\u4e00-\u9fa5]{2,10}|CS\\-CoreLib$";
    public static final File MODULE_FOLDER = new File("CnUsername");
    private static boolean debug;
    private static String pattern = null;

    static {
        try {
            if (MODULE_FOLDER.exists() && MODULE_FOLDER.isFile()) {
                throw new IllegalStateException("错误: 服务端根目录下已存在CnUsername文件，且非文件夹");
            }
            if (!MODULE_FOLDER.exists()) {
                MODULE_FOLDER.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPattern() {
        if (pattern == null) loadConfig();
        return pattern;
    }

    public static void loadConfig() {
        try {
            File debugFile = new File(MODULE_FOLDER, "debug.txt");
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
            File patternFile = new File(MODULE_FOLDER, "pattern.txt");
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
