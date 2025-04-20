package me.xpyex.module.cnusername.impl;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class CUClassVisitor extends ClassVisitor {
    private final String className;
    public final boolean canLoad = this.canLoad();

    protected CUClassVisitor(String className, ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor);
        this.className = className;
    }

    public String getClassName() {
        return className;
        //
    }

    protected boolean canLoad() {
        return true;
    }

    public final MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (canLoad) {
            return onVisitMethod(access, name, descriptor, signature, exceptions);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public abstract MethodVisitor onVisitMethod(int access, String name, String descriptor, String signature, String[] exceptions);
}
