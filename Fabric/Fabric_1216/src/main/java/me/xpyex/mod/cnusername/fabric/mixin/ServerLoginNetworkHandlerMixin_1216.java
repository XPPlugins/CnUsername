package me.xpyex.mod.cnusername.fabric.mixin;

import me.xpyex.module.cnusername.CnUsernameConfig;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin_1216 {
    @Dynamic
    @Inject(method = "isValidName", at = @At("HEAD"), cancellable = true)
    private static void CnUsername$isValidName(String string, CallbackInfoReturnable<Boolean> cir) {
        if (string.matches(CnUsernameConfig.getPattern())) cir.setReturnValue(true);
    }
}
