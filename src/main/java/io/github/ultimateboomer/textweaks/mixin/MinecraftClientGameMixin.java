package io.github.ultimateboomer.textweaks.mixin;

import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.MinecraftClientGame;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClientGame.class)
public class MinecraftClientGameMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onStartGameSession", at = @At("RETURN"))
    private void onOnStartGameSession(CallbackInfo ci) {
        TexTweaks.displayNotice(this.client);
    }
}
