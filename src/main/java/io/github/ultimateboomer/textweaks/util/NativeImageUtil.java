package io.github.ultimateboomer.textweaks.util;

import net.minecraft.client.texture.NativeImage;

public class NativeImageUtil {
	public static NativeImage scaleImage(NativeImage image, int power) {
		if (power == 0) {
			return image;
		} else if (power > 0) {
			return upscaleImageNearest(image, power);
		} else {
			return downscaleImageNearest(image, -power);
		}
	}

	public static NativeImage upscaleImageNearest(NativeImage image, int power) {
		int scale = 1 << power;
		NativeImage newImage = new NativeImage(image.getFormat(), image.getWidth() * scale, image.getHeight() * scale, false);
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				newImage.fillRect(x * scale, y * scale, scale, scale, image.getPixelColor(x, y));
			}
		}
		return newImage;
	}

	public static NativeImage downscaleImageNearest(NativeImage image, int power) {
		int scale = 1 << power;
		NativeImage newImage = new NativeImage(image.getFormat(), image.getWidth() / scale, image.getHeight() / scale, false);
		for (int x = 0; x < newImage.getWidth(); ++x) {
			for (int y = 0; y < newImage.getHeight(); ++y) {
				newImage.setPixelColor(x, y, image.getPixelColor(x * scale, y * scale));
			}
		}
		return newImage;
	}
}
