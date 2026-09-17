package com.example.hitcolour;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class HitcolourClient {
    public static KeyMapping openConfig;

    public static void init() {
        openConfig = new KeyMapping(
                "key.hitcolour.open",
                // 26.3 switched window/input backend from GLFW to SDL3: Type.KEYSYM/
                // SCANCODE merged into KEYBOARD, and GLFW constants removed. Use vanilla
                // InputConstants so the keycode stays correct.
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_H,
                new KeyMapping.Category(Identifier.fromNamespaceAndPath("hitcolour", "category"))
        );

        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.options != null) {
                Field keyMappingsField = mc.options.getClass().getDeclaredField("keyMappings");
                keyMappingsField.setAccessible(true);

                // Use Unsafe to bypass final modifier (Java 22+ removed Field.modifiers access)
                Unsafe unsafe = getUnsafe();
                long offset = unsafe.objectFieldOffset(keyMappingsField);

                KeyMapping[] oldMappings = (KeyMapping[]) keyMappingsField.get(mc.options);
                KeyMapping[] newMappings = new KeyMapping[oldMappings.length + 1];
                System.arraycopy(oldMappings, 0, newMappings, 0, oldMappings.length);
                newMappings[oldMappings.length] = openConfig;
                unsafe.putObject(mc.options, offset, newMappings);
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
