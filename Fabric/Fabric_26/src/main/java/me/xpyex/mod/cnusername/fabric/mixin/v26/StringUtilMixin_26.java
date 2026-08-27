package me.xpyex.mod.cnusername.fabric.mixin.v26;

import me.xpyex.module.cnusername.MixinChecker;
import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringUtil.class)
public class StringUtilMixin_26 {
    @Inject(at = @At("HEAD"), method = "isValidPlayerName", cancellable = true)
    private static void CnUsername$isValidPlayerName(String name, CallbackInfoReturnable<Boolean> cir) {
        Boolean result = MixinChecker.checkPlayerName(name);
        if (result != null) {
            cir.setReturnValue(result);
            cir.cancel();
        }
    }
}
