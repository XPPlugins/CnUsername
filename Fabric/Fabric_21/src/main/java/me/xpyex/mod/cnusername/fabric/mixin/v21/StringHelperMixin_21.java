package me.xpyex.mod.cnusername.fabric.mixin.v21;

import me.xpyex.module.cnusername.CnUsernameConfig;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringHelper.class)
public class StringHelperMixin_21 {
    @Inject(at = @At("HEAD"), method = "isValidPlayerName", cancellable = true)
    private static void CnUsername$isValidPlayerName(String name, CallbackInfoReturnable<Boolean> cir) {
        if (name.trim().isEmpty()) cir.setReturnValue(false);
        if (name.matches(CnUsernameConfig.getPattern())) cir.setReturnValue(true);
    }
}
