package io.github.ultimateboomer.textweaks;

import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.texture.NativeImage;

public abstract class TexTweaksUtil extends RenderPhase {
    public TexTweaksUtil(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    public static NativeImage[] getMipmapLevelsImages(NativeImage image, int mipmap) {
        NativeImage[] nativeImages = new NativeImage[mipmap + 1];
        nativeImages[0] = image;
        if (mipmap > 0) {
            boolean bl = false;

            for(int k = 1; k <= mipmap; ++k) {
                nativeImages[k] = NativeImageUtil.scaleImageLinear(image, Math.pow(0.5, k));
            }
        }

        return nativeImages;
    }

//    public static RenderLayer getEntityCutout(Identifier texture) {
//        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
//                .texture(new RenderPhase.Texture(texture, false, true))
//                .transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
//                .alpha(ONE_TENTH_ALPHA).lightmap(ENABLE_LIGHTMAP)
//                .overlay(ENABLE_OVERLAY_COLOR).build(true);
//        return RenderLayer.of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
//                7, 256, true, false, multiPhaseParameters);
//    }
//
//    public static RenderLayer getEntityCutoutNoCull(Identifier texture) {
//        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
//                .texture(new RenderPhase.Texture(texture, false, false))
//                .transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
//                .alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP)
//                .overlay(ENABLE_OVERLAY_COLOR).build(true);
//        return RenderLayer.of("entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
//                7, 256, true, false, multiPhaseParameters);
//    }
}
