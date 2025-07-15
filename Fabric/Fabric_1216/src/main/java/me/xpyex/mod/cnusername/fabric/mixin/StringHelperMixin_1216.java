package me.xpyex.mod.cnusername.fabric.mixin;

import me.xpyex.module.cnusername.CnUsernameConfig;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(StringHelper.class)
public abstract class StringHelperMixin_1216 {  //StringUtil
    @Inject(method = "isValidPlayerName", at = @At("HEAD"), cancellable = true)
    private static void CnUsername$isValidPlayerName(String name, CallbackInfoReturnable<Boolean> cir) {
        if (name.isEmpty()) cir.setReturnValue(true);  //兼容插件
        if (name.matches(CnUsernameConfig.getPattern())) cir.setReturnValue(true);
    }
}
