package io.github.ultimateboomer.textweaks.util;

import io.github.ultimateboomer.textweaks.TexTweaks;
import io.github.ultimateboomer.textweaks.config.TexTweaksConfig;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.MathHelper;

import java.nio.ByteBuffer;

public class NativeImageUtil {
	public static NativeImage scaleImage(NativeImage image, int power) {
		if (power == 0) {
			return image;
		} else if (power > 0) {
			return upscaleImageNearest(image, power);
		} else {
			switch (TexTweaks.config.textureScaling.downscaleAlgorithm) {
				case NEAREST:
					return downscaleImageNearest(image, -power);
				case LINEAR:
					return downscaleImageLinear(image, -power);
			}
		}
		return null;
	}

	public static NativeImage upscaleImageNearest(NativeImage image, int power) {
		int scale = 1 << power;
		NativeImage newImage = new NativeImage(image.getFormat(),
				image.getWidth() * scale, image.getHeight() * scale, false);
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				newImage.fillRect(x * scale, y * scale, scale, scale, image.getPixelColor(x, y));
			}
		}
		return newImage;
	}

	public static NativeImage downscaleImageNearest(NativeImage image, int power) {
		int scale = 1 << power;
		NativeImage newImage = new NativeImage(image.getFormat(),
				image.getWidth() / scale, image.getHeight() / scale, false);
		for (int x = 0; x < newImage.getWidth(); ++x) {
			for (int y = 0; y < newImage.getHeight(); ++y) {
				newImage.setPixelColor(x, y, image.getPixelColor(x * scale, y * scale));
			}
		}
		return newImage;
	}

	public static NativeImage downscaleImageLinear(NativeImage image, int power) {
		int scale = 1 << power;
		NativeImage newImage = new NativeImage(NativeImage.Format.ABGR,
				image.getWidth() / scale, image.getHeight() / scale, false);
		for (int x = 0; x < newImage.getWidth(); ++x) {
			for (int y = 0; y < newImage.getHeight(); ++y) {
				long r = 0;
				long g = 0;
				long b = 0;
				long a = 0;
				ByteBuffer buffer = ByteBuffer.allocate(4);
				for (int x1 = 0; x1 < scale; ++x1) {
					for (int y1 = 0; y1 < scale; ++y1) {
						buffer.putInt(image.getPixelColor(x * scale + x1, y * scale + y1));
						r += Math.pow(Byte.toUnsignedInt(buffer.get(0)), 2);
						g += Math.pow(Byte.toUnsignedInt(buffer.get(1)), 2);
						b += Math.pow(Byte.toUnsignedInt(buffer.get(2)), 2);
						a += Math.pow(Byte.toUnsignedInt(buffer.get(3)), 2);
						buffer.clear();
					}
				}
				double n = scale * scale;
				int newColor = (MathHelper.floor(Math.sqrt(r / n)) << 24)
						+ (MathHelper.floor(Math.sqrt(g / n)) << 16)
						+ (MathHelper.floor(Math.sqrt(b / n)) << 8)
						+ MathHelper.floor(Math.sqrt(a / n));

				newImage.setPixelColor(x, y, newColor);
			}
		}
		return newImage;
	}
}
