package io.github.ultimateboomer.textweaks.config;

import io.github.ultimateboomer.textweaks.TexTweaks;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Config(name = TexTweaks.MOD_ID)
public class TexTweaksConfig implements ConfigData {
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public TextureScaling textureScaling= new TextureScaling();
	
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public BetterMipmaps betterMipmaps = new BetterMipmaps();
	
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public LodBias lodBias = new LodBias();
	
	@ConfigEntry.Gui.Tooltip
	@ConfigEntry.Gui.CollapsibleObject
	public Other other = new Other();
	
	public static class TextureScaling implements ConfigData {
		public boolean enable = true;
		
		@ConfigEntry.Gui.Tooltip
		@ConfigEntry.BoundedDiscrete(min = 1, max = 9)
		public int resolution = 4;
		
		@ConfigEntry.Gui.Tooltip
		public int maxScale = 8;

		@ConfigEntry.Gui.Tooltip
		public boolean downscale = false;

		@ConfigEntry.Gui.Tooltip(count = 3)
		@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
		public DownscaleAlgorithm downscaleAlgorithm = DownscaleAlgorithm.NEAREST;

		public enum DownscaleAlgorithm {
			NEAREST, LINEAR;
		}
	}
	
	public static class BetterMipmaps implements ConfigData {
		public boolean enable = true;
		
		@ConfigEntry.Gui.Tooltip(count = 3)
		@ConfigEntry.BoundedDiscrete(min = 0, max = 9)
		public int level = 9;

		@ConfigEntry.Gui.Tooltip
		public boolean universalMipmap = false;
	}
	
	public static class LodBias implements ConfigData {
		public boolean enable = false;
		
		@ConfigEntry.Gui.Tooltip(count = 2)
		public float value = 0.0f;
	}
	
	public static class Other implements ConfigData {
		@ConfigEntry.Gui.Tooltip
		public List<String> excludedAtlas = Arrays.asList("minecraft:textures/atlas/particles.png",
			"minecraft:textures/atlas/mob_effects.png", "minecraft:textures/atlas/paintings.png");
		
		@ConfigEntry.Gui.Tooltip
		public boolean parallelPreScaling = true;
	}
}
