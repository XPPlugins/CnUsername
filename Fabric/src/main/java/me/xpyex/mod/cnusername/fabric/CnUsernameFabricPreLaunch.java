package me.xpyex.mod.cnusername.fabric;

import me.xpyex.mod.cnusername.fabric.version.VersionHandler;
import me.xpyex.module.cnusername.Logging;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.Mixins;

public class CnUsernameFabricPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        Logging.ColoredConsole.isClient(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
        Logging.info("Now in " + FabricLoader.getInstance().getEnvironmentType() + ". I am Fabric Mod");
        Logging.info("当前版本: " + VersionHandler.getMinecraftVersion());
        Mixins.addConfiguration(VersionHandler.getCurrentMixin());
        Logging.info("Loading mixin config: " + VersionHandler.getCurrentMixin());
    }
}
