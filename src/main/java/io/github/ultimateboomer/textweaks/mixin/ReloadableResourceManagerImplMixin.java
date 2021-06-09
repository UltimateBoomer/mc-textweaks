package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {
    @Inject(method = "reload", at = @At("HEAD"))
    private void onBeginReloadInner(CallbackInfoReturnable<ResourceReload> cir) throws IOException {
        if (TexTweaks.replaceAllImage != null) {
            TexTweaks.replaceAllImage.close();
            TexTweaks.replaceAllImage = null;
        }

        if (TexTweaks.config.other.replaceAllImages) {
            Resource res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(
                    "textweaks", "textures/replaceall.png"));
            TexTweaks.replaceAllImage = NativeImage.read(res.getInputStream());
            res.close();
        }
    }
}
