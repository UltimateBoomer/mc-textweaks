package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin extends RenderPhase {
//    @Unique
//    private static RenderLayer CUTOUT_TEXTWEAKS_MIPPED;

    public RenderLayerMixin(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/RenderLayer$MultiPhaseParameters$Builder;texture(Lnet/minecraft/client/render/RenderPhase$Texture;)Lnet/minecraft/client/render/RenderLayer$MultiPhaseParameters$Builder;"))
    private static RenderLayer.MultiPhaseParameters.Builder onClInit(RenderLayer.MultiPhaseParameters.Builder builder,
                                                              RenderPhase.Texture texture) {
        if (texture.equals(BLOCK_ATLAS_TEXTURE) && TexTweaks.config.betterMipmaps.mipmapBlockCutouts) {
            return builder.texture(MIPMAP_BLOCK_ATLAS_TEXTURE);
        } else {
            return builder.texture(texture);
        }
    }

//    @Redirect(method = "<clinit>",at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/client/render/RenderLayer;of(Ljava/lang/String;Lnet/minecraft/client/render/VertexFormat;IIZZLnet/minecraft/client/render/RenderLayer$MultiPhaseParameters;)Lnet/minecraft/client/render/RenderLayer$MultiPhase;"))
//    private static RenderLayer.MultiPhase onClInit(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize,
//                                         boolean hasCrumbling, boolean translucent,
//                                         RenderLayer.MultiPhaseParameters phases) {
//        if (name.equals("cutout")) {
//            CUTOUT_TEXTWEAKS_MIPPED = RenderLayer.of("cutout_textweaks_mipped", vertexFormat, drawMode, expectedBufferSize, hasCrumbling,
//                    translucent, RenderLayer.MultiPhaseParameters.builder().shadeModel(SMOOTH_SHADE_MODEL)
//                            .lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).alpha(HALF_ALPHA)
//                            .build(true));
//        }
//        return RenderLayer.of(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling,
//                translucent, phases);
//    }


//    @Redirect(method = "getCutout", at = @At(value = "FIELD",
//            target = "Lnet/minecraft/client/render/RenderLayer;CUTOUT:Lnet/minecraft/client/render/RenderLayer;",
//            opcode = Opcodes.GETSTATIC))
//    private static RenderLayer onGetCutOut() {
//        return TexTweaks.config.betterMipmaps.mipmapBlockCutouts ? CUTOUT_MIPPED : CUTOUT;
//    }

    /**
     * @author UltimateBoomer
     */
//    @Overwrite
//    public static RenderLayer getEntityCutout(Identifier texture) {
//        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false, true)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
//        return RenderLayer.of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseParameters);
//    }

    /**
     * @author UltimateBoomer
     */
//    @Overwrite
//    public static RenderLayer getEntitySolid(Identifier texture) {
//        RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false, true)).transparency(NO_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
//        return RenderLayer.of("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, false, multiPhaseParameters);
//    }
}
