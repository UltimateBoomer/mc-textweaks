package com.ultimateboomer.textweaks.config;

import java.util.Arrays;
import java.util.List;

import com.ultimateboomer.textweaks.TexTweaks;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = TexTweaks.MOD_ID)
public class TexTweaksConfig implements ConfigData {
	@Tooltip
	@CollapsibleObject
	public TextureScaling textureScaling= new TextureScaling();
	
	@Tooltip
	@CollapsibleObject
	public BetterMipmaps betterMipmaps = new BetterMipmaps();
	
	@Tooltip
	@CollapsibleObject
	public LodBias lodBias = new LodBias();
	
	@Tooltip
	@CollapsibleObject
	public Other other = new Other();
	
	public static class TextureScaling implements ConfigData {
		public boolean enable = true;
		
		@Tooltip(count = 2)
		@BoundedDiscrete(min = 1, max = 9)
		public int resolution = 4;
		
		@Tooltip(count = 1)
		public int maxScale = 8;
		
	}
	
	public static class BetterMipmaps implements ConfigData {
		public boolean enable = true;
		
		@Tooltip(count = 3)
		@BoundedDiscrete(min = 0, max = 9)
		public int level = 9;
		
		@Tooltip
		public boolean universalMipmap = false;
	}
	
	public static class LodBias implements ConfigData {
		public boolean enable = false;
		
		@Tooltip(count = 2)
		public float value = 0.0f;
	}
	
	public static class Other implements ConfigData {
		@Tooltip
		public List<String> excludedAtlas = Arrays.asList("minecraft:textures/atlas/particles.png",
			"minecraft:textures/atlas/mob_effects.png");
		
		@Tooltip
		public boolean parallelPreScaling = false;
	}
}
