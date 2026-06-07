package me.xpyex.mod.cnusername.neoforge;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Path;
import me.xpyex.module.cnusername.CnUsername;
import me.xpyex.module.cnusername.CnUsernameConfig;
import me.xpyex.module.cnusername.Logging;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CnUsernameNeo.MOD_ID)
public class CnUsernameNeo {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "cnusername_neo";
    private static ModContainer modContainer;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CnUsernameNeo(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        // 保存 ModContainer 引用，用于获取游戏路径
        CnUsernameNeo.modContainer = modContainer;
    }

    private static File getGamePath() {
        // 方案1: FMLLoader.getGamePath() - 标准API
        try {
            Path path = FMLLoader.getGamePath();
            if (path != null) {  // ← 关键：检查null
                return path.toFile();
            }
        } catch (Throwable ignored) {
        }

        // 方案2: 反射调用 getGameDir() - 兼容旧版
        try {
            Method method = FMLLoader.class.getMethod("getGameDir");
            Object result = method.invoke(null);
            if (result instanceof Path) {  // ← 类型检查
                return ((Path) result).toFile();
            }
        } catch (Throwable ignored) {
        }

        // 方案3: user.dir - 当前工作目录（最可靠）
        String userDir = System.getProperty("user.dir");
        if (userDir != null && !userDir.isEmpty()) {
            return new File(userDir);
        }

        // 方案4: ~/.minecraft - 最后的备用
        return new File(System.getProperty("user.home"), ".minecraft");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        Logging.info("Now in Charset: " + Charset.defaultCharset());
        CnUsername.onEnableInfo();
        CnUsernameConfig.setFolder(new File(getGamePath(), "CnUsername"));
        CnUsernameConfig.loadConfig();
        Logging.info("CnUsername已读取配置");
        Logging.info("当前运行在: Minecraft(" + Minecraft.getInstance().getLaunchedVersion() + ") with NeoForged");
        Logging.info("当前启动的是: " + (Logging.ColoredConsole.isClient() ? "客户端" : "服务端"));
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            Logging.ColoredConsole.isClient(true);
        }
    }
}
