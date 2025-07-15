package me.xpyex.mod.cnusername.fabric;

import me.xpyex.module.cnusername.CnUsername;
import me.xpyex.module.cnusername.CnUsernameConfig;
import me.xpyex.module.cnusername.Logging;
import net.fabricmc.api.ModInitializer;

public class CnUsernameFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CnUsername.onEnableInfo();
        CnUsernameConfig.loadConfig();
        Logging.info("CnUsername已读取配置");
        Logging.info("当前使用的正则规则是: " + CnUsernameConfig.getPattern());
    }
}
