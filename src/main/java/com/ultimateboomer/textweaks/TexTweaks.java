package com.ultimateboomer.textweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ultimateboomer.textweaks.config.TexTweaksConfig;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class TexTweaks implements ClientModInitializer {
	public static final String MOD_ID = "textweaks";
	public static final String MOD_NAME = "TexTweaks";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	
	public static TexTweaksConfig config;
	
	@Override
	public void onInitializeClient() {
		AutoConfig.register(TexTweaksConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(TexTweaksConfig.class).getConfig();
	}
}
