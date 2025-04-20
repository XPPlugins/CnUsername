package me.xpyex.module.cnusername.pass;

import me.xpyex.module.cnusername.impl.CUClassVisitor;
import org.objectweb.asm.ClassVisitor;

@FunctionalInterface
public interface Pass {
    CUClassVisitor create(String className, ClassVisitor classVisitor, String pattern);
}
