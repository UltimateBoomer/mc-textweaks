package io.github.ultimateboomer.textweaks;

import net.minecraft.client.texture.NativeImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Util class for NativeImage manipulation.
 */
public final class NativeImageUtil {
    /**
     * Create a new NativeImage that is scaled using {@link STBImageResize}.
     * The original image will not be closed.
     *
     * @param image input image
     * @param mul multiplier
     * @return scaled image
     */
    public static NativeImage scaleImage(NativeImage image, double mul) {
        return scaleImage(image, mul, mul);
    }

    /**
     * Create a new NativeImage that is scaled using {@link STBImageResize}.
     * The original image will not be closed.
     *
     * @param image input image
     * @param widthMul width multiplier
     * @param heightMul height multiplier
     * @return scaled image
     */
    public static NativeImage scaleImage(NativeImage image, double widthMul, double heightMul) {
        if (widthMul == 1.0 && heightMul == 1.0) {
            return cloneImage(image);
        }

        // Prepare new image
        NativeImage newImage = new NativeImage(image.getFormat(), (int) (image.getWidth() * widthMul),
                (int) (image.getHeight() * heightMul), image.isStbImage);

        // Upscale with STBImageResize
        STBImageResize.nstbir_resize_uint8(image.pointer, image.getWidth(), image.getHeight(), 0,
                newImage.pointer, newImage.getWidth(), newImage.getHeight(), 0,
                image.getFormat().getChannelCount());

        return newImage;
    }

    /**
     * Create a new NativeImage that is scaling using nearest neighbor interpolation.
     * The original image will not be closed.
     *
     * @param image input image
     * @param mul multiplier
     * @return scaled image
     */
    public static NativeImage scaleImageNearest(NativeImage image, double mul) {
        return scaleImageNearest(image, mul, mul);
    }

    /**
     * Create a new NativeImage that is scaling using nearest neighbor interpolation.
     * The original image will not be closed.
     *
     * @param image input image
     * @param widthMul width multiplier
     * @param heightMul height multiplier
     * @return scaled image
     */
    public static NativeImage scaleImageNearest(NativeImage image, double widthMul, double heightMul) {
        if (widthMul == 1.0 && heightMul == 1.0) {
            return cloneImage(image);
        }

        // Make copy of original image
        NativeImage newImage = new NativeImage(image.getFormat(), (int) (image.width * widthMul),
                (int) (image.height * heightMul), image.isStbImage);

        // Update image attributes
        if (image.getWidth() * image.getHeight() > newImage.getWidth() * newImage.getHeight()) {
            // Scaled image is larger than original image
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x1 = (int) (x * widthMul); x1 < (x + 1) * widthMul; x1++) {
                        for (int y1 = (int) (y * heightMul); y1 < (y + 1) * heightMul; y1++) {
                            newImage.setColor(x1, y1, image.getColor(x, y));
                        }
                    }
                }
            }
        } else {
            // Scaled image is smaller than original image
            for (int x = 0; x < newImage.getWidth(); x++) {
                for (int y = 0; y < newImage.getHeight(); y++) {
                    newImage.setColor(x, y, image.getColor((int) (x / widthMul), (int) (y / heightMul)));
                }
            }
        }

        return newImage;
    }

    /**
     * Create a new NativeImage that is scaled using linear interpolation.
     * This produces a slightly different result than {@link #scaleImage(NativeImage, double, double)}.
     * For transparency, the least transparent pixel will be used.
     * The original image will not be closed.
     *
     * @param image input image
     * @param mul multiplier
     * @return scaled image
     */
    public static NativeImage scaleImageLinear(NativeImage image, double mul) {
        return scaleImageLinear(image, mul, mul);
    }

    /**
     * Create a new NativeImage that is scaled using linear interpolation.
     * This produces a slightly different result than {@link #scaleImage(NativeImage, double, double)}.
     * For transparency, the the least transparent pixel will be used.
     * The original image will not be closed.
     *
     * @param image input image
     * @param widthMul width multiplier
     * @param heightMul height multiplier
     * @return scaled image
     */
    public static NativeImage scaleImageLinear(NativeImage image, double widthMul, double heightMul) {
        if (widthMul == 1.0 && heightMul == 1.0) {
            return cloneImage(image);
        }

        // Make copy of original image
        NativeImage newImage = new NativeImage(image.getFormat(), (int) (image.getWidth() * widthMul),
                (int) (image.getHeight() * heightMul), image.isStbImage);

        int channels = image.getFormat().getChannelCount();
        int alphaChannel = image.getFormat().getAlphaOffset() / 8;
        boolean hasAlphaChannel = alphaChannel < channels;

        if (newImage.getWidth() * newImage.getHeight() > image.getWidth() * image.getHeight()) {
            // Scaled image is larger than original image
            ByteBuffer buffer = ByteBuffer.allocate(channels);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    ByteBuffer[][] adjacentColors = new ByteBuffer[2][2];

                    for (int x1 = 0; x1 <= 1; x1++) {
                        for (int y1 = 0; y1 <= 1; y1++) {
                            int color = image.getColor((x + x1) % image.getWidth(),
                                    (y + y1) % image.getHeight());
                            adjacentColors[x1][y1] = ByteBuffer.allocate(channels);
                            adjacentColors[x1][y1].order(ByteOrder.LITTLE_ENDIAN);
                            adjacentColors[x1][y1].putInt(color);
                        }
                    }

                    for (int x1 = (int) (x * widthMul); x1 < (x + 1) * widthMul; x1++) {
                        for (int y1 = (int) (y * heightMul); y1 < (y + 1) * heightMul; y1++) {
                            double localX = (x1 - x * widthMul) / widthMul;
                            double localY = (y1 - y * heightMul) / heightMul;

                            int[] totalColor = new int[channels];
                            if (hasAlphaChannel)
                                totalColor[alphaChannel] = 0xFF;

                            for (int x2 = 0; x2 <= 1; x2++) {
                                for (int y2 = 0; y2 <= 1; y2++) {
                                    for (int i = 0; i < channels; i++) {
                                        ByteBuffer color = adjacentColors[x2][y2];
                                        int f = Byte.toUnsignedInt(color.get(i));
                                        if (i != alphaChannel) {

                                            f *= (x2 == 1 ? localX : (1 - localX));
                                            f *= (y2 == 1 ? localY : (1 - localY));
                                            totalColor[i] += f;
                                        } else {
                                            totalColor[i] = adjacentColors[0][0].get(i);
                                        }


                                    }
                                }
                            }
                            for (int i = 0; i < channels; i++) {
                                buffer.put((byte) totalColor[i]);
                            }

                            buffer.flip();
                            newImage.setColor(x1, y1, buffer.getInt());
                            buffer.clear();
                        }
                    }
                }
            }

        } else {
            // Scaled image is smaller than original image
            ByteBuffer buffer = ByteBuffer.allocate(channels);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            for (int x = 0; x < newImage.getWidth(); x++) {
                for (int y = 0; y < newImage.getHeight(); y++) {
                    int i = 0;
                    int[] totalColor = new int[channels];

                    for (int x1 = (int) (x / widthMul); x1 < (x + 1) / widthMul; x1++) {
                        for (int y1 = (int) (y / heightMul); y1 < (y + 1) / heightMul; y1++) {
                            buffer.putInt(image.getColor(x1, y1));

                            if (hasAlphaChannel && buffer.get(alphaChannel) == 0) {
                                buffer.clear();
                                continue;
                            }

                            for (int j = 0; j < channels; j++) {
                                if (j == alphaChannel) {
                                    totalColor[j] = Math.max(Byte.toUnsignedInt(buffer.get(j)), totalColor[j]);
                                } else {
                                    int c = Byte.toUnsignedInt(buffer.get(j));
                                    totalColor[j] += c * c;
                                }
                            }

                            ++i;
                            buffer.clear();
                        }
                    }

                    if (i > 0) {
                        for (int j = 0; j < channels; j++) {
                            if (j == alphaChannel) {
                                buffer.put((byte) totalColor[j]);
                            } else {
                                buffer.put((byte) (Math.sqrt((double) totalColor[j] / i)));
                            }
                        }
                        buffer.flip();
                        newImage.setColor(x, y, buffer.getInt());
                        buffer.clear();
                    }
                }
            }
        }
        return newImage;
    }

    /**
     * Replace the contents of the image with the other image.
     *
     * @param image target image
     * @param replacer replacer image
     * @param keepTransparency whether to use original transparency for masking
     */
    public static void replaceImage(NativeImage image, NativeImage replacer, boolean keepTransparency) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int color = replacer.getColor(x % replacer.getWidth(), y % replacer.getHeight());
                if (keepTransparency) {
                    color &= 0x00FFFFFF;
                    color += image.getOpacity(x, y) << 24;
                }
                image.setColor(x, y, color);
            }
        }
    }

    /**
     * Clone the NativeImage object and its contents. A new pointer will be created.
     *
     * @param image input
     * @return cloned image
     */
    public static NativeImage cloneImage(NativeImage image) {
        NativeImage newImage = new NativeImage(image.getFormat(), image.getWidth(), image.getHeight(),
                image.isStbImage);
        MemoryUtil.memCopy(image.pointer, newImage.pointer, image.sizeBytes);
        return newImage;
    }

    /**
     * Create an int[][] array representing the pixels of the NativeImage.
     *
     * @param image input
     * @return array
     */
    public static int[][] toArray(NativeImage image) {
        image.checkAllocated();

        int width = image.getWidth();
        int height = image.getHeight();
        int channels = image.getFormat().getChannelCount();

        int[][] array = new int[width][height];
        ByteBuffer buffer = ByteBuffer.allocate(channels);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int offset = (x + y * image.width) * channels;

                for (int i = 0; i < channels; ++i) {
                    buffer.put(MemoryUtil.memGetByte(image.pointer + offset + i));
                }

                array[x][y] = buffer.getInt();
                buffer.clear();
            }
        }

        return array;
    }

    /**
     * Write color array to image. Supports any format.
     *
     * @param image input
     * @param array color array, must have same width and height as input
     */
    public static void writeArrayToImage(NativeImage image, int[][] array) {
        int width = image.getWidth();
        int height = image.getHeight();
        int channels = image.getFormat().getChannelCount();

        if (array.length != width || array[0].length != height) {
            throw new IllegalArgumentException("Dimensions of array and image does not match");
        }

        ByteBuffer buffer = ByteBuffer.allocate(channels);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                buffer.putInt(array[x][y]);
                int offset = (x + y * image.width) * channels;

                for (int i = 0; i < channels; ++i) {
                    MemoryUtil.memPutByte(image.pointer + offset + i, buffer.get(i));
                }

                buffer.clear();
            }
        }
    }
}
