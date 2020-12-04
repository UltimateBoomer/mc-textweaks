package com.ultimateboomer.textweaks;

import com.ultimateboomer.textweaks.config.TexTweaksConfig;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;

public class TexTweaksModMenu implements ModMenuApi {
	@Override
	public String getModId() {
		return TexTweaks.MOD_ID;
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfig.getConfigScreen(TexTweaksConfig.class, parent).get();
	}
}
