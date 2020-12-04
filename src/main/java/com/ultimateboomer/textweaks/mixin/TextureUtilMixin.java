package com.ultimateboomer.textweaks.mixin;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ultimateboomer.textweaks.TexTweaks;

import net.minecraft.client.texture.TextureUtil;

@Mixin(TextureUtil.class)
public class TextureUtilMixin {
	@Shadow
	private static void bind(int id) {};
	
	// Set mipmap LOD bias
	@Redirect(method = "allocate(Lnet/minecraft/client/texture/NativeImage$GLFormat;IIII)V",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;texParameter(IIF)V", ordinal = 0))
	private static void setLodBias(int target, int pname, float param) {
		if (TexTweaks.config.enableLodBiasOverride) {
			if (target == GL11.GL_TEXTURE_2D && pname == GL14.GL_TEXTURE_LOD_BIAS) {
				GlStateManager.texParameter(target, pname, TexTweaks.config.lodBias);
			}
		}
	}
}
