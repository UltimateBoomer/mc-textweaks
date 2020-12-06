package com.ultimateboomer.textweaks.util;

import net.minecraft.client.texture.NativeImage;

@SuppressWarnings("unused")
public class NativeImageUtil {
	/**
	 * Upscale image
	 * 
	 * @param image input
	 * @param power times to run scaling (2x each run)
	 * @return upscaled image
	 */
	public static NativeImage upscaleImage(NativeImage image, int power) {
		if (power < 1) {
			return image;
		}
		
		return upscaleImageFast(image, power);
	}
	
	@Deprecated
	private static NativeImage upscaleImageRecursive(NativeImage image, int power) {
		NativeImage newImage = new NativeImage(image.getFormat(), image.getWidth() * 2, image.getHeight() * 2, true);
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				int pixelColor = image.getPixelColor(x, y);
				newImage.setPixelColor(x * 2, y * 2, pixelColor);
				newImage.setPixelColor(x * 2 + 1, y * 2, pixelColor);
				newImage.setPixelColor(x * 2, y * 2 + 1, pixelColor);
				newImage.setPixelColor(x * 2 + 1, y * 2 + 1, pixelColor);
			}
		}
		image.close();
		if (power == 1) {
			return newImage;
		} else {
			return upscaleImageRecursive(newImage, power - 1);
		}

	}
	
	private static NativeImage upscaleImageFast(NativeImage image, int power) {
		int scale = 1 << power;
		NativeImage newImage = new NativeImage(image.getFormat(), image.getWidth() * scale, image.getHeight() * scale, true);
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				newImage.fillRect(x * scale, y * scale, scale, scale, image.getPixelColor(x, y));
			}
		}
		image.close();
		return newImage;
	}
}
