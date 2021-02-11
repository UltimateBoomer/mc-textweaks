package io.github.ultimateboomer.textweaks.config;

import io.github.ultimateboomer.textweaks.TexTweaks;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

import java.util.Arrays;
import java.util.List;

@Config(name = TexTweaks.MOD_ID)
public class TexTweaksConfig implements ConfigData {
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public TextureScaling textureScaling = new TextureScaling();
	
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public BetterMipmaps betterMipmaps = new BetterMipmaps();
	
	@ConfigEntry.Gui.Tooltip(count = 2)
	@ConfigEntry.Gui.CollapsibleObject
	public LodBias lodBias = new LodBias();
	
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public Other other = new Other();
	
	public static class TextureScaling implements ConfigData {
		public boolean enableUpscale = false;

		public boolean enableDownscale = false;
		
		@ConfigEntry.Gui.Tooltip(count = 2)
		@ConfigEntry.BoundedDiscrete(min = 0, max = 9)
		public int resolution = 4;

		@ConfigEntry.Gui.Tooltip(count = 3)
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		public ScalingAlgorithm upscaleAlgorithm = ScalingAlgorithm.NEAREST;

		@ConfigEntry.Gui.Tooltip(count = 3)
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		public ScalingAlgorithm downscaleAlgorithm = ScalingAlgorithm.LINEAR;

		public enum ScalingAlgorithm {
			NEAREST, LINEAR;
		}

		@ConfigEntry.Gui.Tooltip
		public List<String> upscaleTargetAtlases = Arrays.asList("minecraft:textures/atlas/blocks.png");

		@ConfigEntry.Gui.Tooltip
		public List<String> downscaleTargetAtlases = Arrays.asList("minecraft:textures/atlas/blocks.png",
				"minecraft:textures/atlas/signs.png", "minecraft:textures/atlas/chest.png",
				"minecraft:textures/atlas/beds.png", "minecraft:textures/atlas/shulker_boxes",
				"minecraft:textures/atlas/paintings.png",
				"minecraft:textures/atlas/banner_patterns.png", "minecraft:textures/atlas/shield_patterns");
	}
	
	public static class BetterMipmaps implements ConfigData {
		public boolean enable = true;

		@ConfigEntry.Gui.Tooltip(count = 3)
		@ConfigEntry.BoundedDiscrete(min = 0, max = 9)
		public int level = 9;

		@ConfigEntry.Gui.Tooltip
		public boolean universalMipmap = false;

		@ConfigEntry.Gui.Tooltip(count = 2)
		@ConfigEntry.Gui.RequiresRestart
		public boolean mipmapBlockCutouts = false;

		@ConfigEntry.Gui.Tooltip(count = 2)
		public boolean mipmapChests = false;
	}
	
	public static class LodBias implements ConfigData {
		public boolean enable = false;
		
		@ConfigEntry.Gui.Tooltip(count = 3)
		public float value = 0.0f;
	}
	
	public static class Other implements ConfigData {
		@ConfigEntry.Gui.Tooltip(count = 2)
		public boolean parallelPreScaling = true;

		@ConfigEntry.Gui.Tooltip
		public boolean replaceAllImages = false;
	}
}
