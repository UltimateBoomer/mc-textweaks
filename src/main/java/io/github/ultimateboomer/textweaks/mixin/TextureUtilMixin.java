package io.github.ultimateboomer.textweaks.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import io.github.ultimateboomer.textweaks.TexTweaks;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextureUtil.class)
public class TextureUtilMixin {
	/**
	 * Set mipmap LOD bias
	 */
	@Redirect(method = "prepareImage(Lnet/minecraft/client/texture/NativeImage$InternalFormat;IIII)V",
		at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texParameter(IIF)V"))
	private static void setLodBias(int target, int pname, float param) {
		if (TexTweaks.config.lodBias.enable) {
			if (target == GL11.GL_TEXTURE_2D && pname == GL14.GL_TEXTURE_LOD_BIAS) {
				GlStateManager._texParameter(target, pname, TexTweaks.config.lodBias.value);
			}
		}
	}
}
