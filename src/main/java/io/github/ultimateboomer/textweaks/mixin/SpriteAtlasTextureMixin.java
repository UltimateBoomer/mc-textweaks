package io.github.ultimateboomer.textweaks.mixin;

import com.google.common.collect.Lists;
import io.github.ultimateboomer.textweaks.TexTweaks;
import io.github.ultimateboomer.textweaks.util.NativeImageUtil;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin extends AbstractTexture {
	private static final ThreadLocal<Boolean> SCALE_TEX = ThreadLocal.withInitial(() -> false);

	private static final Map<String, Integer> SCALE_MAP = new ConcurrentHashMap<>();
	
	private static final Consumer<Sprite.Info> PRESCALE_SPRITE = info -> {
		AnimationResourceMetadata anim = info.animationData;
		int w = anim.getWidth(info.getWidth());
		int h = anim.getHeight(info.getHeight());
		
		int size = Math.min(w, h);
		int res = 1 << TexTweaks.config.textureScaling.resolution;

		if (size == res) {
			return;
		}

		boolean upscale;
		if (TexTweaks.config.textureScaling.downscale) {
			if (res < size) {
				upscale = false;
			} else {
				upscale = true;
			}
		} else {
			upscale = true;
		}

		double scale = res / (double) size;
		scale = Math.min(scale, 1 << TexTweaks.config.textureScaling.maxScale); // Clamp to max scale

		if (upscale) {
			scale = MathHelper.clamp(scale, 1, 1 << Math.max(TexTweaks.config.betterMipmaps.level - 4, 1));

			info.width *= scale;
			info.height *= scale;

			//SCALE_MAP.put(info.getId().toString(), (int) (Math.log(scale) / Math.log(2)));
		} else {
			info.width *= scale;
			info.height *= scale;
			if (info.animationData.width > 0) {
				info.animationData.width *= scale;
				info.animationData.height *= scale;
			}


		}
		SCALE_MAP.put(info.getId().toString(), (int) (Math.log(scale) / Math.log(2)));

//		TexTweaks.LOGGER.debug("Pre-scale {} {}x {}x{} -> {}x{} A{}x{}", info.getId().toString(), scale, w, h,
//				info.width, info.height, info.animationData.getWidth(-1), info.animationData.getHeight(-1));

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

	/**
	 * Modify mipmap level value and pass mipmap parameter
	 */
	@ModifyVariable(method = "stitch", at = @At("HEAD"), ordinal = 0)
	private int onStitch(int mipmapLevel) {
		if (!TexTweaks.config.textureScaling.targetAtlases.contains(id.toString())) {
			TexTweaks.LOGGER.debug("Skipped {}-atlas: excluded", id.toString());
			SCALE_TEX.set(false);
			return mipmapLevel;
		}

		SCALE_TEX.set(false);

		if (TexTweaks.config.betterMipmaps.enable) {
			if (mipmapLevel != 0 || TexTweaks.config.betterMipmaps.universalMipmap) {
				mipmapLevel = TexTweaks.config.betterMipmaps.level;
			}
		}
		
		if (TexTweaks.config.textureScaling.enable) {
			if (mipmapLevel != 0) {
				SCALE_TEX.set(true);
			}
		}
		
		return mipmapLevel;
	}

	@Inject(method = "stitch", at = @At("RETURN"))
	private void onStitchReturn(CallbackInfoReturnable<SpriteAtlasTexture.Data> ci) {
		SpriteAtlasTexture.Data data = ci.getReturnValue();
		TexTweaks.dataMap.put(id.toString(), data);
	}

	/**
	 * Change texture size in sprite infos to prepare texture scaling
	 */
	@Inject(method = "loadSprites(Lnet/minecraft/resource/ResourceManager;Ljava/util/Set;)Ljava/util/Collection;",
		at = @At("RETURN"))
	private void onLoadSprites(ResourceManager resourceManager, Set<Identifier> ids,
							   CallbackInfoReturnable<Collection<Sprite.Info>> ci) {
		if (TexTweaks.config.textureScaling.enable) {
			if (SCALE_TEX.get()) {
				TexTweaks.LOGGER.debug("Preparing to scale {}-atlas", id);
				Collection<Sprite.Info> returnValue = ci.getReturnValue();
				
				if (TexTweaks.config.other.parallelPreScaling) {
					//TexTweaks.LOGGER.debug("Parallel pre-scaling is enabled");
					
					List<CompletableFuture<Void>> tasks = Lists.newArrayList();
					returnValue.forEach(info -> {
						tasks.add(CompletableFuture.runAsync(() -> PRESCALE_SPRITE.accept(info),
								Util.getMainWorkerExecutor()));
					});
					
					CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
				} else {
					returnValue.forEach(PRESCALE_SPRITE);
				}
				
				TexTweaks.LOGGER.debug("Pre-scaled {}-atlas", id);
			} else {
				TexTweaks.LOGGER.debug("Skipped {}-atlas: not mipmapped", id);
			}
		}
	}

	/**
	 * Fix mipmapping of non power of 2 textures
	 * May cause problems
	 */
	@Redirect(method = "stitch",
		at = @At(value = "INVOKE", target = "Ljava/lang/Integer;lowestOneBit(I)I"))
	private int onStitchLowestOneBit(int i) {
		return i;
	}

	/**
	 * Scale NativeImage sprite
	 */
	@Redirect(method = "loadSprite",
		at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/texture/NativeImage;read(Ljava/io/InputStream;)" +
						"Lnet/minecraft/client/texture/NativeImage;"))
	private NativeImage onReadSprite(InputStream in, ResourceManager container, Sprite.Info info, int atlasWidth,
									 int atlasHeight, int maxLevel, int x, int y) throws IOException {
		if (TexTweaks.config.textureScaling.enable && maxLevel > 1 && SCALE_MAP.containsKey(info.getId().toString())) {
			NativeImage image = NativeImage.read(in);
			int w = image.getWidth();
			int h = image.getHeight();
	        //double scale = info.getWidth() / image.getWidth();
			int scalePow = SCALE_MAP.get(info.getId().toString());
			double scale = Math.pow(2, scalePow);

			SCALE_MAP.remove(info.getId().toString());
	        if (scalePow != 0) {
	        	//info.width = (int) (image.getWidth() * scale);
	        	//info.height = (int) (image.getHeight() * scale);

	        	TexTweaks.LOGGER.debug("Scale {} {}x {}x{} -> {}x{}", info.getId().toString(), scale, w, h,
						info.width, info.height);
				return NativeImageUtil.scaleImage(image, scalePow);
	        } else {
	        	return image;
	        }
			
		} else {
			return NativeImage.read(in);
		}
	}
}
