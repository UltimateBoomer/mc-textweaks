package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow public abstract CompletableFuture<Void> reloadResources();

    @Shadow public abstract ToastManager getToastManager();

    @Inject(method = "method_31186", at = @At("HEAD"), cancellable = true)
    private void onResourcePackLoadError(Throwable throwable, Text text, CallbackInfo ci) {
        if (TexTweaks.config.other.disableResourcePackReloadOnError) {
            TexTweaks.LOGGER.info("Caught error loading resourcepacks", throwable);

            SystemToast.show(this.getToastManager(), SystemToast.Type.PACK_LOAD_FAILURE,
                    new TranslatableText("resourcePack.load_fail"), text);

            ci.cancel();
        }
    }
}
