package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.textweaks.NativeImageUtil;
import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.texture.MipmapHelper;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MipmapHelper.class)
public class MipmapHelperMixin {
    @Inject(method = "getMipmapLevelsImages", at = @At("HEAD"), cancellable = true)
    private static void onGetMipmapLevelsImages(NativeImage image, int mipmap,
                                                CallbackInfoReturnable<NativeImage[]> ci) {
        if (TexTweaks.config.betterMipmaps.overrideMipmapGeneration) {
            NativeImage[] nativeImages = new NativeImage[mipmap + 1];
            nativeImages[0] = image;
            if (mipmap > 0) {
                boolean bl = false;

                int k;
                label51:
                for(k = 0; k < image.getWidth(); ++k) {
                    for(int j = 0; j < image.getHeight(); ++j) {
                        if (image.getColor(k, j) >> 24 == 0) {
                            bl = true;
                            break label51;
                        }
                    }
                }


                for(k = 1; k <= mipmap; ++k) {
                    NativeImage nativeImage = nativeImages[k - 1];
                    NativeImage nativeImage2 = NativeImageUtil.scaleImageLinear(nativeImage, 0.5);

                    nativeImages[k] = nativeImage2;
                }
            }
            ci.setReturnValue(nativeImages);
        }
    }
}
