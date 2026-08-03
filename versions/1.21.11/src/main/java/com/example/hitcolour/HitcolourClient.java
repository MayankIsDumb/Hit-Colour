package com.example.hitcolour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class HitcolourClient {
    public static KeyBinding openConfig;

    public static void init() {
        openConfig = new KeyBinding(
                "key.hitcolour.open",
                GLFW.GLFW_KEY_H,
                KeyBinding.Category.MISC
        );

        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.options != null) {
                Field keyMappingsField = GameOptions.class.getDeclaredField("allKeys");
                keyMappingsField.setAccessible(true);

                // Use Unsafe to bypass the final modifier (Java 22+ removed Field.modifiers access).
                Unsafe unsafe = getUnsafe();
                long offset = unsafe.objectFieldOffset(keyMappingsField);

                KeyBinding[] oldMappings = (KeyBinding[]) keyMappingsField.get(mc.options);
                KeyBinding[] newMappings = new KeyBinding[oldMappings.length + 1];
                System.arraycopy(oldMappings, 0, newMappings, 0, oldMappings.length);
                newMappings[oldMappings.length] = openConfig;
                unsafe.putObject(mc.options, offset, newMappings);

                // Rebuild the key-code -> binding lookup so key presses reach the new binding.
                KeyBinding.updateKeysByCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }
}