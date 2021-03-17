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
        TexTweaks.initConfig();

        if (texture.equals(BLOCK_ATLAS_TEXTURE) && TexTweaks.config.betterMipmaps.mipmapBlockCutouts) {
            return builder.texture(MIPMAP_BLOCK_ATLAS_TEXTURE);
        } else {
            return builder.texture(texture);
        }
    }
}
