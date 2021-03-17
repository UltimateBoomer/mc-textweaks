package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.niapi.NativeImageUtil;
import io.github.ultimateboomer.textweaks.TexTweaks;
import io.github.ultimateboomer.textweaks.config.TexTweaksConfig;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(SpriteAtlasTexture.class)
public abstract class SpriteAtlasTextureMixin extends AbstractTexture {
	private static final Map<Identifier, Double> SCALE_MAP = new ConcurrentHashMap<>();

	@Shadow @Final private Identifier id;

	/**
	 * Modify mipmap level value and pass mipmap parameter
	 */
	@ModifyVariable(method = "stitch", at = @At("HEAD"), ordinal = 0)
	private int onStitch(int mipmapLevel) {
		if (TexTweaks.config.betterMipmaps.enable) {
			if (mipmapLevel != 0 || TexTweaks.config.betterMipmaps.universalMipmap) {
				mipmapLevel = TexTweaks.config.betterMipmaps.level;
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
							   CallbackInfoReturnable<Collection<Sprite.Info>> ci) throws Exception {
		if (TexTweaks.config.textureScaling.enableUpscale || TexTweaks.config.textureScaling.enableDownscale) {
			boolean upscale = TexTweaks.config.textureScaling.enableUpscale
					&& TexTweaks.config.textureScaling.upscaleTargetAtlases.stream().anyMatch(s ->
					this.id.toString().startsWith(s));

			boolean downscale = TexTweaks.config.textureScaling.enableDownscale
					&& TexTweaks.config.textureScaling.downscaleTargetAtlases.stream().anyMatch(s ->
					this.id.toString().startsWith(s));

			if (upscale || downscale) {
				TexTweaks.LOGGER.debug("Preparing to scale {}-atlas", id);
				Collection<Sprite.Info> returnValue = ci.getReturnValue();

				CompletableFuture.runAsync(() -> returnValue.parallelStream().forEach(info ->
						prescaleSprite(info, upscale, downscale)), Util.getMainWorkerExecutor()).get();

				TexTweaks.LOGGER.info("Pre-scaled {}-atlas", id);
			} else {
				TexTweaks.LOGGER.info("Skipped scaling {}-atlas", id);
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
		NativeImage image = NativeImage.read(in);
		if (TexTweaks.replaceAllImage != null) {
			NativeImageUtil.replaceImage(image, TexTweaks.replaceAllImage, true);
		}

		if ((TexTweaks.config.textureScaling.enableUpscale || TexTweaks.config.textureScaling.enableDownscale)
				&& SCALE_MAP.containsKey(info.getId())) {
			int w = image.getWidth();
			int h = image.getHeight();
			double scale = SCALE_MAP.get(info.getId());

			SCALE_MAP.remove(info.getId());
	        TexTweaks.LOGGER.debug("Scale {} {}x {}x{} -> {}x{}", info.getId().toString(), scale, w, h,
					info.width, info.height);

			TexTweaksConfig.TextureScaling.ScalingAlgorithm algorithm;
			if (scale > 1.0) {
				algorithm = TexTweaks.config.textureScaling.upscaleAlgorithm;
			} else {
				algorithm = TexTweaks.config.textureScaling.downscaleAlgorithm;
			}

			NativeImage newImage;
			if (algorithm.equals(TexTweaksConfig.TextureScaling.ScalingAlgorithm.NEAREST)) {
				newImage = NativeImageUtil.scaleImageNearest(image, scale);
			} else {
				newImage = NativeImageUtil.scaleImageLinear(image, scale);
			}
			image.close();
			image = newImage;
		}

		return image;
	}

	private static void prescaleSprite(Sprite.Info info, boolean upscale, boolean downscale) {
		AnimationResourceMetadata anim = info.animationData;
		int w = anim.getWidth(info.getWidth());
		int h = anim.getHeight(info.getHeight());

		int size = Math.min(w, h);
		int res = 1 << TexTweaks.config.textureScaling.resolution;

		if (size == res) {
			return;
		}

		double scale = (double) res / size;

		if (scale > 1.0 && !upscale) {
			return;
		}

		if (scale < 1.0 && !downscale) {
			return;
		}

		info.width *= scale;
		info.height *= scale;

		if (info.animationData.width > 0) {
			info.animationData.width *= scale;
			info.animationData.height *= scale;
		}

		SCALE_MAP.put(info.getId(), scale);

	}
}
