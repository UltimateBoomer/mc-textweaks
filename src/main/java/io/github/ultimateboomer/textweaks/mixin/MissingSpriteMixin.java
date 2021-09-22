package io.github.ultimateboomer.textweaks.mixin;

import com.google.common.collect.ImmutableList;
import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MissingSprite.class)
public class MissingSpriteMixin {
    @Shadow
    @Final
    private static Identifier MISSINGNO;

    private static final Lazy<NativeImage> IMAGE_SCALED = new Lazy<NativeImage>(() -> {
        TexTweaks.LOGGER.info("MissingSprite image accessed");
        int size;
        if (TexTweaks.config.textureScaling.enableUpscale) {
            size = 2 << TexTweaks.config.textureScaling.resolution;
        } else {
            size = 16;
        }

        NativeImage nativeImage = new NativeImage(size, size, false);
        int i = -16777216;
        int j = -524040;

        for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
                if (k < 8 ^ l < 8) {
                    nativeImage.setColor(l, k, -524040);
                } else {
                    nativeImage.setColor(l, k, -16777216);
                }
            }
        }

        nativeImage.untrack();
        return nativeImage;
    });

//    @Unique
//    private static Sprite.Info newSpriteInfo() {
//        return new Sprite.Info(MISSINGNO, 16, 16,
//                new AnimationResourceMetadata(ImmutableList.of(new AnimationFrameResourceMetadata(0, -1)),
//                        16, 16, 1, false));
//    }

//    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/Sprite;<init>(Lnet/minecraft/client/texture/SpriteAtlasTexture;Lnet/minecraft/client/texture/Sprite$Info;IIIIILnet/minecraft/client/texture/NativeImage;)V"))
//    private Sprite.Info onInitGetInfo(Sprite.Info info) {
//        return newSpriteInfo();
//    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/Lazy;get()Ljava/lang/Object;"))
    private static Object onGetImage(Lazy lazy, SpriteAtlasTexture spriteAtlasTexture, int maxLevel, int atlasWidth, int atlasHeight, int x, int y) {
        TexTweaks.LOGGER.info("MissingSprite image accessed");
        int size;
        if (maxLevel >= 4 && TexTweaks.config.textureScaling.enableUpscale) {
            size = 2 << TexTweaks.config.textureScaling.resolution;
        } else {
            size = 16;
        }

        NativeImage nativeImage = new NativeImage(size, size, false);
        int i = -16777216;
        int j = -524040;

        for(int k = 0; k < size; ++k) {
            for(int l = 0; l < size; ++l) {
                if (k < 8 ^ l < 8) {
                    nativeImage.setColor(l, k, -524040);
                } else {
                    nativeImage.setColor(l, k, -16777216);
                }
            }
        }

        nativeImage.untrack();
        return nativeImage;
    }

    @Redirect(method = "<init>", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/texture/MissingSprite;INFO:Lnet/minecraft/client/texture/Sprite$Info;"))
    private static Sprite.Info onInitGetInfo(SpriteAtlasTexture spriteAtlasTexture, int maxLevel, int atlasWidth, int atlasHeight, int x, int y) {
        int size;
        if (maxLevel >= 4 && TexTweaks.config.textureScaling.enableUpscale) {
            size = 2 << TexTweaks.config.textureScaling.resolution;
        } else {
            size = 16;
        }

        return new Sprite.Info(MISSINGNO, size, size,
                new AnimationResourceMetadata(ImmutableList.of(new AnimationFrameResourceMetadata(0, -1)),
                        size, size, 1, false));
    }

//    @Redirect(method = "getMissingInfo", at = @At(value = "FIELD",
//            target = "Lnet/minecraft/client/texture/MissingSprite;INFO:Lnet/minecraft/client/texture/Sprite$Info;"))
//    private static Sprite.Info onGetInfo() {
//        int size;
//        if (TexTweaks.config.textureScaling.enableUpscale) {
//            size = 2 << TexTweaks.config.textureScaling.resolution;
//        } else {
//            size = 16;
//        }
//
//        return new Sprite.Info(MISSINGNO, size, size,
//                new AnimationResourceMetadata(ImmutableList.of(new AnimationFrameResourceMetadata(0, -1)),
//                        size, size, 1, false));
//    }
}
