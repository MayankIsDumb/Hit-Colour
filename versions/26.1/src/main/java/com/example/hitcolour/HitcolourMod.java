package com.example.hitcolour;

import com.example.hitcolour.config.HConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HitcolourMod implements ModInitializer {
	public static final String MOD_ID = "hitcolour";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		HConfig.load();
		LOGGER.info("[{}] Hitcolour initialized!", MOD_ID);
	}
}
