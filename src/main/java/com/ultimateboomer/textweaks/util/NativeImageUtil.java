package com.ultimateboomer.textweaks.util;

import com.ultimateboomer.textweaks.TexTweaks;

import net.minecraft.client.texture.NativeImage;

public class NativeImageUtil {
	/**
	 * Upscale image recursively
	 * 
	 * @param image input
	 * @param scale times to run scaling (2x each run)
	 * @return upscaled image
	 */
	public static NativeImage upscaleImage(NativeImage image, int scale) {
		if (scale < 1) {
			return image;
		}
		
		NativeImage newImage = new NativeImage(image.getFormat(), image.getWidth() * 2, image.getHeight() * 2, true);
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				int pixelColor = image.getPixelColor(x, y);
				newImage.setPixelColor(x * 2, y * 2, pixelColor);
				newImage.setPixelColor(x * 2 + 1, y * 2, pixelColor);
				newImage.setPixelColor(x * 2, y * 2 + 1, pixelColor);
				newImage.setPixelColor(x * 2 + 1, y * 2 + 1, TexTweaks.config.debugFractal ? 0xFF000000 : pixelColor);
			}
		}
		//MipmapTweaks.LOGGER.info("Upscaled texture " + scale);
		
		image.close();
		if (scale == 1) {
			return newImage;
		} else {
			return upscaleImage(newImage, scale - 1);
		}
		
	}
}
