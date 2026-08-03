package com.example.hitcolour.mixin;

import com.example.hitcolour.config.HConfig;
import com.example.hitcolour.listener.OverlayReloadListener;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayTexture.class)
public abstract class MixinOverlayTexture implements OverlayReloadListener {
    // Vanilla red hit-flash, packed in NativeImage's AABG byte order: alpha=0xB2, red=0xFF.
    @Unique
    private static final int VANILLA_RED = 0xB20000FF;

    @Shadow
    @Final
    private NativeImageBackedTexture texture;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hitcolour$init(CallbackInfo ci) {
        this.reloadOverlay();
        OverlayReloadListener.register(this);
    }

    @Override
    public void onOverlayReload() {
        this.reloadOverlay();
    }

    @Unique
    private void reloadOverlay() {
        NativeImage pixels = this.texture.getImage();
        if (pixels == null) {
            return;
        }

        HConfig config = HConfig.CONFIG.instance();
        // The entity shader does mix(overlayColor.rgb, color.rgb, overlayColor.a), so a LOWER
        // alpha means a STRONGER flash. Invert so that "100%" = full flash, "0%" = none.
        int alpha;
        int red;
        int green;
        int blue;
        if (config.enable) {
            int mapped = (int) ((config.opacity / 100.0f) * 255.0F);
            alpha = 255 - mapped;
            red = config.color.getRed();
            green = config.color.getGreen();
            blue = config.color.getBlue();
        } else {
            alpha = (VANILLA_RED >>> 24) & 0xFF;
            red = (VANILLA_RED >>> 16) & 0xFF;
            green = (VANILLA_RED >>> 8) & 0xFF;
            blue = VANILLA_RED & 0xFF;
        }

        int color = (alpha << 24) | (blue << 16) | (green << 8) | red;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 16; ++j) {
                pixels.setColor(j, i, color);
            }
        }
        this.texture.upload();
    }
}