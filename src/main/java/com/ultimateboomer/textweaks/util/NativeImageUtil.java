package com.ultimateboomer.textweaks.util;

import net.minecraft.client.texture.NativeImage;

public class NativeImageUtil {
	public static NativeImage upscaleImageFast(NativeImage image, int power) {
		int scale = 1 << power;
		NativeImage newImage = new NativeImage(image.getFormat(), image.getWidth() * scale, image.getHeight() * scale, false);
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				newImage.fillRect(x * scale, y * scale, scale, scale, image.getPixelColor(x, y));
			}
		}
		return newImage;
	}
}
