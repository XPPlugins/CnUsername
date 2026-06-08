package me.xpyex.plugin.cnusername;

import bot.inker.acj.JvmHacker;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import me.xpyex.module.cnusername.ClassTransformer;
import me.xpyex.module.cnusername.CnUsernameConfig;
import me.xpyex.module.cnusername.Logging;
import me.xpyex.module.cnusername.pass.PassRegistry;

public interface CnUsernamePlugin {
    AtomicReference<MethodHandle> DEFINE_CLASS_METHOD = new AtomicReference<>();

    static MethodHandle getDefineClassMethod() {
        if (DEFINE_CLASS_METHOD.get() == null) {
            try {
                DEFINE_CLASS_METHOD.set(JvmHacker.lookup().findVirtual(
                    ClassLoader.class,
                    "defineClass",
                    MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class)
                ));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("初始化失败", e);
            }
        }
        return DEFINE_CLASS_METHOD.get();
    }

    default Instrumentation instrumentationOrNull() {
        try {
            return JvmHacker.instrumentation();
        } catch (Throwable e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("Permission denied") || errorMsg.contains("Read-only file system"))) {
                Logging.warning("检测到容器环境限制(只读文件系统/权限不足)，无法动态加载JavaAgent");
                Logging.warning("当前将使用直接加载字节码的方案");
                Logging.warning("建议解决方案: ");
                Logging.warning("1. 使用 普通JavaAgent 启动，详见ReadMe");
                Logging.warning("2. 确保容器 tmp 目录可写");
            } else if (errorMsg != null && errorMsg.contains("tools.jar")) {
                Logging.warning("未找到 tools.jar，当前可能运行在JRE而非JDK，无法动态加载JavaAgent");
                Logging.warning("当前将使用直接加载字节码的方案");
                Logging.warning("建议解决方案: ");
                Logging.warning("1. 使用 JDK 而非 JRE");
                Logging.warning("2. 使用 普通JavaAgent 启动，详见ReadMe");
            } else {
                Logging.warning("无法获取Instrumentation实例: " + e);
                if (CnUsernameConfig.isDebug()) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    default void applyInstrumentation(Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                return CnUsernamePlugin.this.transform(loader, className, classBeingRedefined, classfileBuffer);
            }
        }, true);

        Map<String, Class<?>> loadedClasses = Arrays.stream(instrumentation.getAllLoadedClasses())
                                                  .collect(Collectors.toMap(
                                                      clazz -> clazz.getName().replace('.', '/'),
                                                      clazz -> clazz,
                                                      (existing, replacement) -> existing  // 遇到重复key时保留第一个
                                                  ));

        Set<Class<?>> pendingRetransformClasses = PassRegistry.allPossibleClasses().stream()
                                                      .filter(loadedClasses::containsKey)
                                                      .map(loadedClasses::get)
                                                      .collect(Collectors.toSet());

        for (Class<?> retransformClass : pendingRetransformClasses) {
            try {
                instrumentation.retransformClasses(retransformClass);
            } catch (UnmodifiableClassException e) {
                Logging.warning("无法重定义类 " + retransformClass.getName() + ": " + e);
            }
        }
    }

    default byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, byte[] classfileBuffer) {
        return ClassTransformer.transform(
            loader,
            className,
            classBeingRedefined,
            classfileBuffer
        );
    }
}
