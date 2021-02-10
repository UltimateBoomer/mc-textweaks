package io.github.ultimateboomer.textweaks.mixin;

import net.minecraft.client.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractTexture.class)
public class AbstractTextureMixin {
//    @ModifyVariable(method = "setFilter", at = @At("HEAD"), ordinal = 1)
//    private boolean onSetTexture(boolean mipmap) {
//        return true;
//    }
}
