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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import me.xpyex.module.cnusername.ClassTransformer;
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
            Logging.warning("无法获取Instrumentation实例: " + e);
            e.printStackTrace();
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

        Set<Class<?>> pendingRetransformClasses = new LinkedHashSet<>();

        for (String possibleClassName : PassRegistry.allPossibleClasses()) {
            Class<?> loadedClass = loadedClasses.get(possibleClassName);
            if (loadedClass != null) {
                pendingRetransformClasses.add(loadedClass);
            }
        }

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
