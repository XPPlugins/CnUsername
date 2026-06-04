package me.xpyex.mod.cnusername.neoforge.mixin;

import com.mojang.brigadier.StringReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StringReader.class, priority = 1000)
public class StringReaderMixin {
    @Inject(require = 0, method = "isAllowedInUnquotedString", at = @At("HEAD"), cancellable = true, remap = false)
    private static void CnUsername$isAllowedInUnquotedString(char c, CallbackInfoReturnable<Boolean> cir) {
        if (c >= '一' && c <= '龥') {
            cir.setReturnValue(true);  //所有中文字符
            cir.cancel();
        }
    }
}
