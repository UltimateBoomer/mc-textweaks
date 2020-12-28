package io.github.ultimateboomer.textweaks.mixin;

import com.google.common.collect.Lists;
import io.github.ultimateboomer.textweaks.TexTweaks;
import io.github.ultimateboomer.textweaks.util.NativeImageUtil;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin extends AbstractTexture {
	private static final ThreadLocal<Boolean> STITCH_MIPMAP = ThreadLocal.withInitial(() -> false);
	
	private static final Consumer<Sprite.Info> PRESCALE_SPRITE = info -> {
		int w = info.width;
		int h = info.height;
		
		int size = Math.min(w, h);
		int scale = (int) Math.ceil((1 << TexTweaks.config.textureScaling.resolution) / (double) size); // Round up
		scale = Math.min(scale, 1 << TexTweaks.config.textureScaling.maxScale);
		scale = MathHelper.clamp(scale, 1, 1 << Math.max(TexTweaks.config.betterMipmaps.level - 4, 1));
		
		info.width = w * scale;
		info.height = h * scale;
	};

	@Shadow
	@Final
	private Map<Identifier, Sprite> sprites;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	private Identifier id;

	@Shadow
	private Collection<Sprite.Info> loadSprites(ResourceManager resourceManager, Set<Identifier> ids) { return null; }
	
	// Modify mipmap level value and pass mipmap parameter
	@ModifyVariable(method = "stitch", at = @At("HEAD"), ordinal = 0)
	private int onStitch(int mipmapLevel) {
		this.bilinear = true;
		if (TexTweaks.config.other.excludedAtlas.contains(id.toString())) {
			return mipmapLevel;
		}
		
		if (TexTweaks.config.betterMipmaps.enable) {
			if (mipmapLevel != 0 || TexTweaks.config.betterMipmaps.universalMipmap) {

				mipmapLevel = Math.min(TexTweaks.config.betterMipmaps.level, TexTweaks.config.textureScaling.resolution);
			}
		}
		
		if (TexTweaks.config.textureScaling.enable) {
			if (mipmapLevel != 0) {
				STITCH_MIPMAP.set(true);
			}
		}
		
		return mipmapLevel;
	}
	
	// Change texture size in sprite infos to prepare texture scaling
	@Inject(method = "Lnet/minecraft/client/texture/SpriteAtlasTexture;loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;", 
		at = @At("RETURN"))
	private void onLoadSprites(ResourceManager resourceManager, Set<Identifier> ids, CallbackInfoReturnable<Collection<Sprite.Info>> ci) {
		if (TexTweaks.config.textureScaling.enable) {
			if (STITCH_MIPMAP.get()) {
				TexTweaks.LOGGER.debug("Preparing to scale {}-atlas", id);
				Collection<Sprite.Info> returnValue = ci.getReturnValue();
				
				if (TexTweaks.config.other.parallelPreScaling) {
					TexTweaks.LOGGER.debug("Parallel pre-scaling is enabled");
					
					List<CompletableFuture<Void>> tasks = Lists.newArrayList();
					returnValue.forEach(info -> {
						tasks.add(CompletableFuture.runAsync(() -> PRESCALE_SPRITE.accept(info)));
					});
					
					CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
				} else {
					returnValue.forEach(PRESCALE_SPRITE);
				}
				
				TexTweaks.LOGGER.debug("Pre-scaled {}-atlas", id);
			} else {
				TexTweaks.LOGGER.debug("Skipped scaling {}-atlas because it is not mipmapped", id);
			}
		}
	}
	
	// Fix mipmapping of non power of 2 textures
	// May cause problems
	@Redirect(method = "stitch",
		at = @At(value = "INVOKE", target = "Ljava/lang/Integer;lowestOneBit(I)I"))
	private int onStitchLowestOneBit(int i) {
		return i;
	}
	
	// Scale NativeImage sprite
	@Redirect(method = "loadSprite",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;read(Ljava/io/InputStream;)Lnet/minecraft/client/texture/NativeImage;"))
	private NativeImage onReadSprite(InputStream in, ResourceManager container, Sprite.Info info, int atlasWidth, int atlasHeight, int maxLevel, int x, int y) throws IOException {
		if (TexTweaks.config.textureScaling.enable) {
			NativeImage image = NativeImage.read(in);
			int w = image.getWidth();
			int h = image.getHeight();
	        int scale = info.getWidth() / image.getWidth();
	        if (scale > 1) {
	        	TexTweaks.LOGGER.debug("Scale {} {}x {}x{} -> {}x{}", info.getId().toString(), scale, w, h, info.width, info.height);
				return NativeImageUtil.upscaleImageFast(image, MathHelper.log2(scale));
	        } else {
	        	return image;
	        }
			
		} else {
			return NativeImage.read(in);
		}
	}

//	@Overwrite
//	public void applyTextureFilter(SpriteAtlasTexture.Data data) {
//		TexTweaks.LOGGER.debug(data);
//		this.setFilter(true, data.maxLevel > 0);
//	}

//	@Override
//	public void setFilter(boolean bilinear, boolean mipmap) {
//		this.bilinear = bilinear;
//		this.mipmap = mipmap;
//		int k;
//		short l;
//		if (bilinear) {
//			k = mipmap ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR;
//			l = GL11.GL_NEAREST;
//		} else {
//			k = mipmap ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST;
//			l = GL11.GL_NEAREST;
//		}
//		GlStateManager.texParameter();
//		GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, k);
//		GlStateManager.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, l);
//	}


}
