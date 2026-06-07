package me.xpyex.module.cnusername.modify.paper;

import java.lang.Runtime.Version;
import java.util.UUID;
import me.xpyex.module.cnusername.CnUsername;
import me.xpyex.module.cnusername.Logging;
import me.xpyex.module.cnusername.impl.PatternVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Paper在{@link ClassPlayerProfile#createAuthLibProfile(UUID, String)}中 <br>
 * 在使用{@link StringUtil#isValidPlayerName(String)}之前还检查了一次玩家名长度 <br>
 * CnUsername能够覆写isValidPlayerName(String)，但不可能去覆写String.length()方法 <br>
 * 故选择覆写createAuthLibProfile方法，仅删除检查玩家名长度的部分，其余不变. <br>
 * 注意：Leaf等衍生服务端可能重载了createAuthLibProfile(UUID, String, boolean)方法
 */
public class ClassVisitorCraftPlayerProfile extends PatternVisitor {
    public static final String CLASS_PATH = "com/destroystokyo/paper/profile/CraftPlayerProfile";
    private static boolean leafLoaded = false;

    public ClassVisitorCraftPlayerProfile(String className, ClassVisitor classVisitor, String pattern) {
        super(className, classVisitor, pattern);
    }

    @Override
    protected boolean canLoad() {
        if (Version.parse("1.20.5").compareToIgnoreOptional(CnUsername.getMcVersion()) > 0) {
            Logging.info("服务端处于§e1.20.5以下§r版本，无需修改CraftPlayerProfile类");
            return false;
        }
        return true;
    }

    @Override
    public MethodVisitor onVisitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor visitor = super.cv.visitMethod(access, name, descriptor, signature, exceptions);

        // 匹配 createAuthLibProfile 静态方法
        if ("createAuthLibProfile".equals(name) && (access & Opcodes.ACC_STATIC) > 0) {
            // 三参数版本(Leaf等衍生服务端)
            if ("(Ljava/util/UUID;Ljava/lang/String;Z)Lcom/mojang/authlib/GameProfile;".equals(descriptor)) {
                Logging.info("正在修改 " + getClassName() + " 类中的 " + name + "(UUID, String, boolean) 方法 [Leaf]");
                leafLoaded = true;
                return createRemoveLengthCheckVisitor(visitor);
            }
            // 两参数版本(原版Paper)
            if ("(Ljava/util/UUID;Ljava/lang/String;)Lcom/mojang/authlib/GameProfile;".equals(descriptor) && !leafLoaded) {
                Logging.info("正在修改 " + getClassName() + " 类中的 " + name + "(UUID, String) 方法 [Paper]");
                return createRemoveLengthCheckVisitor(visitor);
            }
        }

        return visitor;
    }

    /**
     * 创建一个删除长度检查的MethodVisitor
     * 通过丢弃从方法开始到第一个checkArgument调用之间的所有字节码来实现
     */
    private MethodVisitor createRemoveLengthCheckVisitor(MethodVisitor originalVisitor) {
        return new MethodVisitor(Opcodes.ASM9, originalVisitor) {
            private boolean foundCheckArgument = false;

            @Override
            public void visitCode() {
                super.visitCode();
                // 暂时丢弃所有后续指令，直到找到checkArgument
                mv = null;
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                // 如果还没找到checkArgument，先写入这条指令
                if (!foundCheckArgument) {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

                    // 检测到checkArgument静态方法调用，恢复正常的字节码写入
                    if (Opcodes.INVOKESTATIC == opcode && "checkArgument".equals(name)) {
                        foundCheckArgument = true;
                        mv = originalVisitor;
                    }
                } else {
                    // 已经找到checkArgument，正常写入所有指令
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
            }

            @Override
            public void visitEnd() {
                // 如果整个方法都没找到checkArgument，说明方法结构不符合预期，不修改
                if (!foundCheckArgument) {
                    Logging.warning("未在方法中找到checkArgument调用，跳过修改以避免破坏方法结构");
                    mv = originalVisitor;
                }
                super.visitEnd();
            }
        };
    }
}
