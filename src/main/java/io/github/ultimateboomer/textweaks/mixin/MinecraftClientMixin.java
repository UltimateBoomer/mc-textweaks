package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow public abstract CompletableFuture<Void> reloadResources();

    @Shadow public abstract ToastManager getToastManager();

    @Shadow @Nullable public ClientWorld world;

    @Inject(method = "onResourceReloadFailure", at = @At("HEAD"), cancellable = true)
    private void onResourceReloadFailure(Throwable throwable, Text text, CallbackInfo ci) {
        if (TexTweaks.config.other.disableResourcePackReloadOnError) {
            TexTweaks.LOGGER.info("Caught error loading resourcepacks", throwable);

            SystemToast.show(this.getToastManager(), SystemToast.Type.PACK_LOAD_FAILURE,
                    Text.translatable("resourcePack.load_fail"), text);

            ci.cancel();
        }
    }

    @Inject(method = "reloadResources(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    private void onReloadResources(CallbackInfoReturnable<CompletableFuture<Void>> ci) {
        if (this.world != null) {
            TexTweaks.displayNotice((MinecraftClient) (Object) this);
        }
    }
}
