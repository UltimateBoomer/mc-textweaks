package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.textweaks.TexTweaks;
import io.github.ultimateboomer.textweaks.TexTweaksUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(ChestBlockEntityRenderer.class)
public class ChestBlockEntityRendererMixin {
//    @Redirect(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;"))
//    private VertexConsumer onRender(SpriteIdentifier spriteIdentifier, VertexConsumerProvider vertexConsumers, Function<Identifier, RenderLayer> layerFactory) {
//        if (TexTweaks.config.betterMipmaps.mipmapChests) {
//            return spriteIdentifier.getVertexConsumer(vertexConsumers, TexTweaksUtil::getEntityCutout);
//        } else {
//            return spriteIdentifier.getVertexConsumer(vertexConsumers, layerFactory);
//        }
//    }
}
