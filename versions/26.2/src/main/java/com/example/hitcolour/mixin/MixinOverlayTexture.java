package com.example.hitcolour.mixin;

import com.example.hitcolour.config.HConfig;
import com.example.hitcolour.listener.OverlayReloadListener;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Recolors the entity hit-flash (the red wash that appears on a mob/player
 * when they take damage) using the configured color, instead of a screen flash.
 */
@Mixin(OverlayTexture.class)
public abstract class MixinOverlayTexture implements OverlayReloadListener {
    /** The vanilla semi-transparent red hit-flash, restored when the mod is disabled. */
    @Unique
    private static final int VANILLA_RED = 0xB2FF0000;

    @Shadow
    @Final
    private DynamicTexture texture;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hitcolour$init(CallbackInfo ci) {
        this.reloadColor();
        OverlayReloadListener.register(this);
    }

    @Unique
    private void reloadColor() {
        NativeImage pixels = this.texture.getPixels();
        if (pixels == null) {
            return;
        }

        HConfig config = HConfig.CONFIG.instance();
        // The entity shader does mix(overlay, texture, overlayAlpha), so a LOWER alpha
        // means a STRONGER flash. Invert the slider's alpha so "100%" = full flash.
        int alpha;
        if (config.enable) {
            int mapped = (int) ((config.opacity / 100.0f) * 255.0F);
            alpha = 255 - mapped;
        } else {
            alpha = (VANILLA_RED >>> 24) & 0xFF;
        }
        int rgb = config.enable ? (config.color.getRGB() & 0xFFFFFF) : (VANILLA_RED & 0xFFFFFF);
        int argb = (alpha << 24) | rgb;

        // Rows 0-7 hold the red (now recolored) hit-flash cells.
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 16; ++j) {
                pixels.setPixel(j, i, argb);
            }
        }
        this.texture.upload();
    }

    @Override
    public void onOverlayReload() {
        this.reloadColor();
    }
}