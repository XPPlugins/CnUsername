package me.xpyex.mod.cnusername.fabric;

import java.io.File;
import java.nio.charset.Charset;
import me.xpyex.module.cnusername.CnUsername;
import me.xpyex.module.cnusername.CnUsernameConfig;
import me.xpyex.module.cnusername.Logging;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CnUsernameFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Logging.ColoredConsole.isClient(true);
        Logging.info("Now in Charset: " + Charset.defaultCharset());
        CnUsername.onEnableInfo();
        CnUsernameConfig.setFolder(new File(FabricLoader.getInstance().getConfigDir().getParent().toFile(), "CnUsername"));
        CnUsernameConfig.loadConfig();
        Logging.info("CnUsername已读取配置");
        Logging.info("当前使用的正则规则是: " + CnUsernameConfig.getPattern());
        Logging.info("This is Fabric Mod");
    }
}
