package me.xpyex.module.cnusername;

import me.xpyex.module.cnusername.impl.CUClassVisitor;
import me.xpyex.module.cnusername.pass.Pass;
import me.xpyex.module.cnusername.pass.PassRegistry;
import me.xpyex.module.cnusername.pass.RetransformPass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * 类转换工具类，提供统一的字节码转换逻辑
 */
public class ClassTransformer {

    /**
     * 执行类转换的核心逻辑
     *
     * @param loader              类加载器
     * @param className           类名（使用 / 分隔符）
     * @param classBeingRedefined 被重定义的类
     * @param classfileBuffer     原始字节码
     * @param pattern             正则表达式模式
     * @param excludePrefixes     需要排除的包名前缀数组
     * @param computeFlags        ClassWriter的计算标志位
     * @return 转换后的字节码，如果不需要转换则返回null
     */
    public static byte[] transform(
        ClassLoader loader,
        String className,
        Class<?> classBeingRedefined,
        byte[] classfileBuffer,
        String pattern,
        String[] excludePrefixes,
        int computeFlags
    ) {
        // fail fast, ensure not loop load
        if (loader == null) {
            return null;
        }

        for (String prefix : excludePrefixes) {
            if (className.startsWith(prefix)) {
                return null;
            }
        }

        Pass pass = PassRegistry.getPass(className);
        if (pass == null || PassRegistry.isModified(className)) {
            return null;
        }

        if (pass instanceof RetransformPass) {
            ((RetransformPass) pass).retransform(classBeingRedefined, pattern);
        }

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter classWriter = new ClassWriter(reader, computeFlags);
        CUClassVisitor classVisitor = pass.create(className.replace('/', '.'), classWriter, pattern);
        if (!classVisitor.canLoad) {
            return null;
        }

        reader.accept(classVisitor, 0);
        byte[] modifiedClassfileBuffer = classWriter.toByteArray();

        if (CnUsernameConfig.isDebug()) {
            try {
                Logging.info("Debug模式开启，保存修改后的样本以供调试");
                Logging.info("已保存 " + className + " 类的文件样本至: " + CnUsername.saveClassFile(modifiedClassfileBuffer, className).getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PassRegistry.setModified(className);
        return modifiedClassfileBuffer;
    }

    /**
     * 统一的转换方法（推荐直接使用此方法）
     */
    public static byte[] transform(
        ClassLoader loader,
        String className,
        Class<?> classBeingRedefined,
        byte[] classfileBuffer
    ) {
        return transform(
            loader,
            className,
            classBeingRedefined,
            classfileBuffer,
            CnUsernameConfig.getPattern(),
            new String[]{"me/xpyex/module/cnusername/", "me/xpyex/plugin/cnusername/"},
            ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
        );
    }
}
