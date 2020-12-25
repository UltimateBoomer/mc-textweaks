package com.ultimateboomer.textweaks.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.texture.TextureUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextureUtil.class)
public class TextureUtilMixin {
	@ModifyVariable(method = "allocate(Lnet/minecraft/client/texture/NativeImage$GLFormat;IIII)V",
		at = @At("HEAD"), ordinal = 1)
	private static int onAllocate(int maxLevel) {
		if (maxLevel == 0) {
			return 4;
		} else {
			return maxLevel;
		}
	}
	
	// Set mipmap LOD bias
	@Redirect(method = "allocate(Lnet/minecraft/client/texture/NativeImage$GLFormat;IIII)V",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;texParameter(IIF)V"))
	private static void setLodBias(int target, int pname, float param) {
		if (TexTweaks.config.lodBias.enable) {
			if (target == GL11.GL_TEXTURE_2D && pname == GL14.GL_TEXTURE_LOD_BIAS) {
				GlStateManager.texParameter(target, pname, TexTweaks.config.lodBias.value);
			}
		}
	}
}
