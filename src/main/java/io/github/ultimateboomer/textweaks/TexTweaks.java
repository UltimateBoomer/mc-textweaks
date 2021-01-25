package io.github.ultimateboomer.textweaks;

import io.github.ultimateboomer.textweaks.config.TexTweaksConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public class TexTweaks implements ClientModInitializer {
	public static final String MOD_ID = "textweaks";
	public static final String MOD_NAME = "TexTweaks";
	
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final Map<String, SpriteAtlasTexture.Data> dataMap = new LinkedHashMap<>();
	
	public static TexTweaksConfig config;

	private static KeyBinding keyInfo;
	
	@Override
	public void onInitializeClient() {
		// Register config
		AutoConfig.register(TexTweaksConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(TexTweaksConfig.class).getConfig();

		// Register singleplayer command
		//CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> TexTweaksCommand.register(dispatcher)));

		// Register keybindings
		keyInfo = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.textweaks.info",
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.textweaks.main"));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyInfo.wasPressed()) {
				if (client.player != null) {
					displayInfo(client);
				}
			}
		});
	}

	public static void displayInfo(MinecraftClient mc) {
		mc.player.sendMessage(new LiteralText(""), false);
		mc.player.sendMessage(new LiteralText("\u00a7lTexture Info\u00a7r"), false);

		dataMap.forEach((id, data) -> {
			mc.player.sendMessage(new LiteralText(
					String.format("Atlas: \u00a7e%s\u00a7r", id.toString())), false);
			mc.player.sendMessage(new LiteralText(
					String.format(" - Size: \u00a7a%s\u00a7rx\u00a7a%s\u00a7r", data.width, data.height)),
					false);
			mc.player.sendMessage(new LiteralText(
					String.format(" - # of textures: \u00a7b%s\u00a7r", data.spriteIds.size())), false);
			mc.player.sendMessage(new LiteralText(
					String.format(" - Mipmap Level: \u00a7a%s\u00a7r", data.maxLevel)), false);
		});
	}
}
