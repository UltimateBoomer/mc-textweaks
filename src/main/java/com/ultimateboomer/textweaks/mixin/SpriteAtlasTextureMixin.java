package com.ultimateboomer.textweaks.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.ultimateboomer.textweaks.TexTweaks;
import com.ultimateboomer.textweaks.util.NativeImageUtil;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {
	private static final ThreadLocal<Boolean> stitchMipmap = new ThreadLocal<Boolean>();
	
	@Shadow
	@Final
	private static Logger LOGGER;
	
	@Shadow
	@Final
	private Identifier id;
	
	@Shadow
	private Collection<Sprite.Info> loadSprites(ResourceManager resourceManager, Set<Identifier> ids) { return null; }
	
	// Edit mipmap level value and pass mipmap parameter
	@ModifyVariable(method = "stitch", at = @At("HEAD"), ordinal = 0)
	private int onStitch(int mipmapLevel) {
		if (TexTweaks.config.enableMipmapOverride) {
			if (mipmapLevel == 0) {
				mipmapLevel = 0;
			} else {
				mipmapLevel = TexTweaks.config.maxMipmap;
			}
		}
		
		if (TexTweaks.config.enableTextureScaling) {
			if (mipmapLevel == 0) {
				stitchMipmap.set(false);
			} else {
				stitchMipmap.set(true);
			}
		}
		
		return mipmapLevel;
	}
	
	// Change texture size in sprite infos to prepare texture scaling
	@Inject(method = "Lnet/minecraft/client/texture/SpriteAtlasTexture;loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;", 
		at = @At("RETURN"))
	private void onStitchLoadSprites(ResourceManager resourceManager, Set<Identifier> ids, CallbackInfoReturnable<Collection<Sprite.Info>> ci) {
		if (TexTweaks.config.enableTextureScaling) {
			if (stitchMipmap.get()) {
				TexTweaks.LOGGER.debug("Scaling {}-atlas", id);
				ci.getReturnValue().forEach(info -> {
					int w = info.width;
					int h = info.height;
					
					int size = Math.min(w, h);
					int scale = (1 << TexTweaks.config.textureResolution) / size;
					scale = Math.min(scale, 1 << TexTweaks.config.maxScale);
					if (scale != 1 << MathHelper.log2(scale)) {
						scale = 1;
					}
					scale = MathHelper.clamp(scale, 1, 1 << Math.max(TexTweaks.config.maxMipmap - 4, 1));
					info.width = w * scale;
					info.height = h * scale;
					TexTweaks.LOGGER.debug("Pre-scaled {} {}x {}x{} -> {}x{}", info.getId().toString(), scale, w, h, info.width, info.height);
				});
			} else {
				TexTweaks.LOGGER.debug("Skipped scaling {}-atlas because it is not mipmapped", id);
			}
		}
	}
	
	// Scale NativeImage sprite
	@Redirect(method = "loadSprite",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;read(Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;"))
	private NativeImage onReadSprite(InputStream in, ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y) throws IOException {
		if (TexTweaks.config.enableTextureScaling) {
			NativeImage image = NativeImage.read(in);
			int w = image.getWidth();
			int h = image.getHeight();
	        int scale = info.getWidth() / image.getWidth();
			TexTweaks.LOGGER.debug("Scaled {} {}x {}x{} -> {}x{}", info.getId().toString(), scale, w, h, info.width, info.height);
			return NativeImageUtil.upscaleImage(image, MathHelper.log2(scale));
		} else {
			return NativeImage.read(in);
		}
		
	}
}
