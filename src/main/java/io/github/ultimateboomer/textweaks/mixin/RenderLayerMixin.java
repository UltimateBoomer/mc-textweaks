package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin extends RenderPhase {
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
