package me.xpyex.module.cnusername.modify.minecraft;

import me.xpyex.module.cnusername.Logging;
import me.xpyex.module.cnusername.impl.PatternVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassVisitorUtilColor extends PatternVisitor {
    public static final String CLASS_PATH = "net/minecraft/util/UtilColor";

    public ClassVisitorUtilColor(String className, ClassVisitor classVisitor, String pattern) {
        super(className, classVisitor, pattern);
    }

    @Override
    public MethodVisitor onVisitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor visitor = getDefaultMethodVisitor(access, name, descriptor, signature, exceptions);
        if ("f".equals(name) && "(Ljava/lang/String;)Z".equals(descriptor) && (access & Opcodes.ACC_STATIC) > 0) {  // static boolean f(String string)
            Logging.info("正在修改 " + getClassName() + " 类中的 " + name + "(String) 方法");
            visitor.visitCode();
            Label label0 = new Label();
            visitor.visitLabel(label0);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitLdcInsn(getPattern());
            visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "matches", "(Ljava/lang/String;)Z", false);
            visitor.visitInsn(Opcodes.IRETURN);
            Label label1 = new Label();
            visitor.visitLabel(label1);
            visitor.visitLocalVariable("string", "Ljava/lang/String;", null, label0, label1, 0);
            visitor.visitMaxs(2, 1);
            visitor.visitEnd();
            return null;
        }
        return visitor;
    }
}
