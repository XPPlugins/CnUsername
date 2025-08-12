package me.xpyex.mod.cnusername.fabric.mixin.v18;

import me.xpyex.module.cnusername.CnUsernameConfig;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandlerMixin_18 {
    @Inject(require = 0, method = "isValidName", at = @At("HEAD"), cancellable = true)
    private static void CnUsername$isValidName(String name, CallbackInfoReturnable<Boolean> cir) {
        if (name.trim().isEmpty()) cir.setReturnValue(false);
        if (name.matches(CnUsernameConfig.getPattern())) cir.setReturnValue(true);
    }
}
