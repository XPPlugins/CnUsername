package me.xpyex.module.cnusername.modify.minecraft;

import me.xpyex.module.cnusername.Logging;
import me.xpyex.module.cnusername.impl.CUClassVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassVisitorPacketDataSerializer extends CUClassVisitor {
    public static final String CLASS_PATH = "net/minecraft/network/PacketDataSerializer";

    public ClassVisitorPacketDataSerializer(String className, ClassVisitor classVisitor) {
        super(className, classVisitor);
    }

    @Override
    public MethodVisitor onVisitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.cv.visitMethod(access, name, descriptor, signature, exceptions);
        if ("(Ljava/lang/String;I)Lnet/minecraft/network/PacketDataSerializer;".equals(descriptor)) {
            Logging.info("正在修改 " + getClassName() + " 类中的 " + name + "(String, int) 方法");
            // 去除方法体内所有throw关键字
            return new MethodVisitor(Opcodes.ASM9, mv) {
                private boolean expectAThrowing = false;

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    // 检测是否调用了EncoderException的构造函数
                    if (opcode == Opcodes.INVOKESPECIAL
                            && "io/netty/handler/codec/EncoderException".equals(owner)
                            && "<init>".equals(name)) {
                        expectAThrowing = true; // 标记下一个ATHROW需要处理
                    }
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ATHROW && expectAThrowing) {
                        // 替换ATHROW为POP，移除异常对象
                        super.visitInsn(Opcodes.POP);
                        expectAThrowing = false;
                    } else {
                        super.visitInsn(opcode);
                    }
                }
            };
        }
        return mv;
    }

}
