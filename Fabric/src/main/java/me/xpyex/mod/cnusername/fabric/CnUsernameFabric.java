package me.xpyex.mod.cnusername.fabric;

import java.io.File;
import java.nio.charset.Charset;
import me.xpyex.mod.cnusername.fabric.version.VersionHandler;
import me.xpyex.module.cnusername.CnUsername;
import me.xpyex.module.cnusername.CnUsernameConfig;
import me.xpyex.module.cnusername.Logging;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CnUsernameFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Logging.info("Now in Charset: " + Charset.defaultCharset());
        Logging.info("RuntimeNamespaces: " + FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace() + "[" + String.join(", ", FabricLoader.getInstance().getMappingResolver().getNamespaces()) + "]");
        CnUsername.onEnableInfo();
        CnUsernameConfig.setFolder(new File(FabricLoader.getInstance().getGameDir().toFile(), "CnUsername"));
        CnUsernameConfig.loadConfig();
        Logging.info("CnUsername已读取配置");

        Logging.info("Detected Minecraft version: " + VersionHandler.getMinecraftVersion());
    }
}
