package me.xpyex.module.cnusername;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {
    public static String version = "";

    static {
        try (InputStream is = UpdateChecker.class.getClassLoader().getResourceAsStream("version")) {  //由Gradle填充的版本文件
            if (is != null) {
                version = readInputStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void check() {
        Logging.info("开始检查更新信息");
        Logging.info("当前版本: §e" + version);
        try {
            String api = "https://api.github.com/repos/XPPlugins/CnUsername/releases/latest";
            URLConnection connection = new URL(api).openConnection();
            connection.setConnectTimeout(5000);  //5s超时

            String result = readInputStream(connection.getInputStream()).replace("\n", "");

            String tagNameAfter = result.substring(result.indexOf("\"tag_name\":") + 11);
            String tagName = tagNameAfter.substring(0, tagNameAfter.indexOf(","))
                                 .replace(",", "")
                                 .replace("\"", "")
                                 .trim();
            String body = result.substring(result.indexOf("\"body\":") + 7)
                              .replace("}", "")
                              .replace("\"", "")
                              .trim();
            if (compareVersion(version, tagName) < 0) {
                Logging.info("发现新版本: §e" + tagName);
                Logging.info("更新内容: " + body.replace("\\r", "").replace("\\n", "\n"));
                Logging.info("§6下载地址§e§o(Github):§r https://github.com/XPPlugins/CnUsername/releases");
            } else if (compareVersion(version, tagName) == 0) {
                Logging.info("当前版本为最新版本");
            } else {
                Logging.info("当前版本高于最新发布版本");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Logging.warning("获取更新失败，但不影响当前使用");
        }
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 比较两个版本号大小，忽略可选的 v 前缀，支持如 1.2.3-beta 的预发布后缀。
     *
     * @return v1 &lt; v2 返回负数；v1 == v2 返回 0；v1 &gt; v2 返回正数
     */
    private static int compareVersion(String v1, String v2) {
        String[] parts1 = v1.replaceFirst("^[vV]", "").trim().split("\\.");
        String[] parts2 = v2.replaceFirst("^[vV]", "").trim().split("\\.");
        int len = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < len; i++) {
            int n1 = i < parts1.length ? parseVersionPart(parts1[i]) : 0;
            int n2 = i < parts2.length ? parseVersionPart(parts2[i]) : 0;
            if (n1 != n2) {
                return Integer.compare(n1, n2);
            }
        }
        return 0;
    }

    private static int parseVersionPart(String part) {
        int i = 0;
        while (i < part.length() && Character.isDigit(part.charAt(i))) {
            i++;
        }
        if (i == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(part.substring(0, i));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
