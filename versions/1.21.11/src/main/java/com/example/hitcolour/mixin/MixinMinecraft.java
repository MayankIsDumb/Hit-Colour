package com.example.hitcolour.mixin;

import com.example.hitcolour.HitcolourClient;
import com.example.hitcolour.config.HCScreen;
import com.example.hitcolour.listener.OverlayReloadListener;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void hitcolour$init(CallbackInfo ci) {
        HitcolourClient.init();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void hitcolour$tick(CallbackInfo ci) {
        // Re-upload the recolored hit-flash every tick so config changes apply live.
        OverlayReloadListener.callEvent();

        MinecraftClient mc = (MinecraftClient) (Object) this;
        if (HitcolourClient.openConfig != null && HitcolourClient.openConfig.wasPressed()) {
            mc.setScreen(HCScreen.configScreen(null));
        }
    }
}