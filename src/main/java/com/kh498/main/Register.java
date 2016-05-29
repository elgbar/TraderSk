package com.kh498.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;

import com.kh498.main.Eff.*;

public class Register extends JavaPlugin {

	@Override
	public void onEnable() {
		Skript.registerAddon(this);
		boolean enabled = false;
		if (Bukkit.getPluginManager().getPlugin("Merchants") != null) {
			if (Skript.isAcceptRegistrations()) {
				Skript.registerEffect(EffTraderCreate.class,
						"(create|make) [a] [merchant] trader [named] %string%");
				Skript.registerEffect(EffTraderCreate.class,
						"(remove|clear) [the] [merchant] trader [named] %string%");
				Skript.registerEffect(
						EffTraderSetPage.class,
						"set items in page %integer% (for|or) [merchant] trader %string% to %itemstack% as output[ item](,| and) %itemstack%[ and %-itemstack%] as input[ item[s]]");
				Skript.registerEffect(EffTraderRemovePage.class,
						"(remove|clear) page %integer% (from|of|for) [merchant] trader %string%");
				Skript.registerEffect(EffTraderOpen.class,
						"(open|show) [merchant] trader %string% to %player%");
				// Skript.registerEffect(EffTraderListPages.class,
				// "list all items (for|in) [merchant] trader %string% to %player%");

				getLogger().info("Merchants effects added!");
				enabled = true;
			} else {
				Skript.error("Could not register effects as skript is not accepting registrations.");
			}
		} else {
			getLogger().info("Could not find Merchants. Plugin not enabled.");
		}
		if (enabled)
			getLogger().info("Enabled ~ Created by kh498");
		else
			getLogger().info("Could not enable the plugin!");
	}
}
