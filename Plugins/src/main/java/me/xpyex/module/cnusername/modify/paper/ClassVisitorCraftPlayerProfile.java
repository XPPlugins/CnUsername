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
 * 故选择覆写createAuthLibProfile(UUID, String)方法，仅删除检查玩家名长度的部分，其余不变. <br>
 */
public class ClassVisitorCraftPlayerProfile extends PatternVisitor {
    public static final String CLASS_PATH = "com/destroystokyo/paper/profile/CraftPlayerProfile";

    public ClassVisitorCraftPlayerProfile(String className, ClassVisitor classVisitor, String pattern) {
        super(className, classVisitor, pattern);
    }

    @Override
    protected boolean canLoad() {
        if (Version.parse("1.20.4").compareToIgnoreOptional(CnUsername.getMcVersion()) >= 0) {
            Logging.info("服务端处于§e1.20.5以下§r版本，无需修改CraftPlayerProfile类");
            return false;
        }
        return true;
    }

    @Override
    public MethodVisitor onVisitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor visitor = super.cv.visitMethod(access, name, descriptor, signature, exceptions);
        if ("createAuthLibProfile".equals(name) && (access & Opcodes.ACC_STATIC) > 0 && "(Ljava/util/UUID;Ljava/lang/String;)Lcom/mojang/authlib/GameProfile;".equals(descriptor)) {
            Logging.info("正在修改 " + getClassName() + " 类中的 " + name + "(UUID, String) 方法");
            return new MethodVisitor(Opcodes.ASM9, visitor) {  //删除该方法内第一行代码 string.length() 的检查
                @Override
                public void visitCode() {
                    super.visitCode();
                    mv = null;
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    if (Opcodes.INVOKESTATIC == opcode && "checkArgument".equals(name)) {
                        mv = visitor;
                    }
                }
            };
        }
        return visitor;
    }
}
