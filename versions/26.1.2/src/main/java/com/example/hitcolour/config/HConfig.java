package com.example.hitcolour.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;

public class HConfig {
    public static final ConfigClassHandler<HConfig> CONFIG = ConfigClassHandler.createBuilder(HConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("hitcolour.json"))
                    .build())
            .build();

    @SerialEntry
    public boolean enable = true;

    @SerialEntry
    public Color color = new Color(255, 0, 0);

    @SerialEntry
    public int opacity = 70;

    public static HConfig load() {
        CONFIG.load();
        return CONFIG.instance();
    }

    public void save() {
        CONFIG.save();
    }
}
