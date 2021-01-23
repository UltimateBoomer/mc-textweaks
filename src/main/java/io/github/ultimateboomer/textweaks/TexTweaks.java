package io.github.ultimateboomer.textweaks;

import io.github.ultimateboomer.textweaks.command.TexTweaksCommand;
import io.github.ultimateboomer.textweaks.config.TexTweaksConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TexTweaks implements ClientModInitializer {
	public static final String MOD_ID = "textweaks";
	public static final String MOD_NAME = "TexTweaks";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	
	public static TexTweaksConfig config;
	
	@Override
	public void onInitializeClient() {
		// Register config
		AutoConfig.register(TexTweaksConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(TexTweaksConfig.class).getConfig();

		// Register singleplayer command
		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> TexTweaksCommand.register(dispatcher)));
	}
}
