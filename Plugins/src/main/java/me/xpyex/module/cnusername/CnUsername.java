package me.xpyex.module.cnusername;

import java.io.File;
import java.io.IOException;
import java.lang.Runtime.Version;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import me.xpyex.module.cnusername.impl.CUClassVisitor;
import me.xpyex.module.cnusername.pass.Pass;
import me.xpyex.module.cnusername.pass.PassRegistry;
import me.xpyex.module.cnusername.pass.RetransformPass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class CnUsername {
    private static Version MC_VERSION = null;

    public static void premain(final String agentArgs, final Instrumentation inst) {
        Logging.debug(agentArgs);
        Logging.info("开始载入模块 §eCnUsername");
        onEnableInfo();
        CnUsernameConfig.loadConfig();
        try {
            Logging.info("开始检查banned-players.json文件，以添加补丁");
            addToBanList("CS-CoreLib");
            Logging.info("补丁应用完成");
        } catch (Exception e) {
            Logging.warning("添加补丁失败: " + e);
            if (CnUsernameConfig.isDebug()) e.printStackTrace();
            Logging.warning("建议服务器启动后手动封禁CS-CoreLib玩家名");
        }
        Logging.info("===========================================================");
        Logging.info("当前服务端运行于: §e" + getMcVersion());
        UpdateChecker.check();
        Logging.info("等待Minecraft加载...");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                // fail fast, ensure not loop load
                if (loader == null || className.startsWith("me/xpyex/module/cnusername/") || className.startsWith("me/xpyex/plugin/cnusername/")) {
                    return null;
                }

                Pass pass = PassRegistry.getPass(className);
                if (pass == null || PassRegistry.isModified(className)) {
                    return null;
                }

                if (pass instanceof RetransformPass) {
                    ((RetransformPass) pass).retransform(classBeingRedefined, agentArgs);
                }

                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter classWriter = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                CUClassVisitor classVisitor = pass.create(className.replace('/', '.'), classWriter, agentArgs);
                if (!classVisitor.canLoad) {
                    return null;
                }

                reader.accept(classVisitor, 0);
                byte[] modifiedClassFileBuffer = classWriter.toByteArray();

                if (CnUsernameConfig.isDebug()) {
                    try {
                        Logging.info("Debug模式开启，保存修改后的样本以供调试");
                        Logging.info("已保存 " + className + " 类的文件样本至: " + saveClassFile(modifiedClassFileBuffer, className).getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                PassRegistry.setModified(className);
                return modifiedClassFileBuffer;
            }
        });
    }

    public static void onEnableInfo() {
        Logging.info("如遇Bug，或需提出建议: §aQQ群546338486 §r| §eQQ1723275529");
        Logging.info("开源地址§6§o(GitHub)§r: https://github.com/XPPlugins/CnUsername");
        Logging.info("有空可以去看看有没有更新噢~");
        Logging.info("===========================================================");
    }

    public static File saveClassFile(ClassWriter writer, String className) throws IOException {
        return saveClassFile(writer.toByteArray(), className);
        //
    }

    public static File saveClassFile(byte[] data, String className) throws IOException {
        File file = new File(CnUsernameConfig.MODULE_FOLDER, className.replace("/", ".") + ".class");
        Files.write(file.toPath(), data);
        return file;
    }

    public static void addToBanList(String name) throws IOException {
        File f = new File("banned-players.json");
        if (f.isDirectory()) {
            throw new IllegalStateException("banned-players.json是个文件夹？\nWhy banned-players.json is a directory?");
        }
        if (!f.exists()) f.createNewFile();
        String content = Files.readString(f.toPath());
        if (content == null || content.trim().isEmpty() || "[]".equals(content.trim())) {
            Logging.info("banned-players.json文件内容为空，执行覆写操作");
            Files.write(f.toPath(), ("[\n" +
                                         "  {\n" +
                                         "    \"uuid\": \"" + UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes()) + "\",\n" +  //只需要考虑离线，正版服务器不会用CnUsername也用不了，第三方验证注册时就不会允许这个
                                         "    \"name\": \"" + name + "\",\n" +
                                         "    \"created\": \"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " +0800\",\n" +
                                         "    \"source\": \"CnUsername\",\n" +
                                         "    \"expires\": \"forever\",\n" +
                                         "    \"reason\": \"Invalid username\"\n" +
                                         "  }\n" +
                                         "]").getBytes());
        } else if (!content.contains(name)) {
            Logging.info("banned-players.json文件内不存在 " + name + " 玩家，执行添加操作");
            Files.write(f.toPath(), (content.substring(0, content.length() - 1)  //去掉最后一个右中括号
                                         + ",\n" +  //JsonArray新增
                                         "  {\n" +
                                         "    \"uuid\": \"" + UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes()) + "\",\n" +
                                         "    \"name\": \"" + name + "\",\n" +
                                         "    \"created\": \"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " +0800\",\n" +
                                         "    \"source\": \"CnUsername\",\n" +
                                         "    \"expires\": \"forever\",\n" +
                                         "    \"reason\": \"Invalid username\"\n" +
                                         "  }\n" +
                                         "]"
            ).getBytes());
        }
    }

    public static Version getMcVersion() {
        if (MC_VERSION == null) {
            File properties = new File("server.properties").getAbsoluteFile();
            files:
            for (File file : properties.getParentFile().listFiles()) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    try (JarFile jar = new JarFile(file)) {
                        Enumeration<JarEntry> enumFiles = jar.entries();
                        while (enumFiles.hasMoreElements()) {
                            JarEntry entry = enumFiles.nextElement();
                            String entryName = entry.getName();
                            if (entryName.contains("META-INF/versions/1.") && entryName.endsWith("/")) {
                                String[] split = entryName.split("/");
                                MC_VERSION = Version.parse(split[split.length - 1]);
                                break files;
                            } else if (entryName.contains("META-INF/versions/") && (entryName.endsWith(".jar") || entryName.endsWith(".jar.patch"))) {
                                String[] split = entryName.split("/");
                                MC_VERSION = Version.parse(split[split.length - 1].split("-")[1]);
                                break files;
                            }
                        }
                    } catch (Exception e) {
                        if (CnUsernameConfig.isDebug()) e.printStackTrace();
                    }
                }
            }
        }
        return MC_VERSION;
    }
}