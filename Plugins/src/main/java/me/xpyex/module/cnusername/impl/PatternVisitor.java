package me.xpyex.module.cnusername.impl;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import me.xpyex.module.cnusername.CnUsernameConfig;
import me.xpyex.module.cnusername.Logging;
import org.objectweb.asm.ClassVisitor;

public abstract class PatternVisitor extends CUClassVisitor {
    private final String pattern;

    protected PatternVisitor(String className, ClassVisitor classVisitor, String pattern) {
        super(className, classVisitor);
        String s;
        if (pattern == null || pattern.isEmpty()) {
            s = CnUsernameConfig.DEFAULT_PATTERN;
            Logging.info("当前玩家名规则将使用本组件的默认正则规则");
        } else {
            try {
                Pattern.compile(pattern);
                s = pattern;
            } catch (PatternSyntaxException e) {
                s = CnUsernameConfig.DEFAULT_PATTERN;
                e.printStackTrace();
                Logging.warning("你自定义的正则格式无效: " + pattern);
                Logging.info("当前玩家名规则将使用本组件的默认正则规则");
                Logging.info("不用担心，该错误不会影响本组件正常工作，但你需要改改你写的正则规则了 :)");
            }
        }
        Logging.info("当前组件使用的正则规则为: §6" + s);
        this.pattern = s;
    }

    public String getPattern() {
        return pattern;
        //
    }
}
