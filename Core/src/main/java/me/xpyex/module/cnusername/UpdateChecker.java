package me.xpyex.module.cnusername;

import java.io.ByteArrayOutputStream;
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
            if (!("v" + version).equalsIgnoreCase(tagName)) {
                Logging.info("发现新版本: §e" + tagName);
                Logging.info("更新内容: " + body.replace("\\r", "").replace("\\n", "\n"));
                Logging.info("§6下载地址§e§o(Github):§r https://github.com/XPPlugins/CnUsername/releases");
            } else {
                Logging.info("当前版本为最新版本");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Logging.warning("获取更新失败，但不影响当前使用");
        }
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
