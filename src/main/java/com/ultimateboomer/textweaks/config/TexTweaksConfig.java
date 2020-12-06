package com.ultimateboomer.textweaks.config;

import java.util.Arrays;
import java.util.List;

import com.ultimateboomer.textweaks.TexTweaks;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = TexTweaks.MOD_ID)
public class TexTweaksConfig implements ConfigData {
	@Tooltip
	public boolean enableTextureScaling = true;
	
	@Tooltip(count = 2)
	@BoundedDiscrete(min = 1, max = 9)
	public int textureResolution = 4;
	
	@Tooltip(count = 1)
	public int maxScale = 8;
	
	public List<String> excludedAtlas = Arrays.asList("minecraft:textures/atlas/particles.png",
		"minecraft:textures/atlas/mob_effects.png");
	
	public boolean enableMipmapOverride = true;
	
	@Tooltip(count = 2)
	@BoundedDiscrete(min = 0, max = 9)
	public int maxMipmap = 9;
	
	public boolean enableUniversalMipmap = true;
	
	public boolean enableLodBiasOverride = false;
	
	@Tooltip(count = 3)
	public float lodBias = 0.0f;
}
