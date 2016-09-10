/**
 *  This file is part of TraderSk
 *  
 *  Copyright (C) 2016, kh498
 * 
 *  TraderSk is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TraderSk is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TraderSk.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kh498.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.kh498.main.util.Util;

import ch.njol.skript.Skript;
import me.cybermaxke.merchants.SMerchantPlugin;

/**
 * @author kh498
 */
public class Main extends JavaPlugin
{
	private boolean enabled; //Used by onDisable to not save the traders if the plugin never fully enables
	public String MCversion = Util.getNmsVersion(); //Minecraft server version
	private static Plugin instance;

	public static Plugin getInstance()
	{
		return instance;
	}

	@Override
	public void onEnable()
	{
		if (Bukkit.getPluginManager().getPlugin("Skript") == null)
		{
			this.getServer().getConsoleSender().sendMessage("[" + this.getName() + "] " + ChatColor.RED
					+ "Could not enable the plugin! This is an addon to Skript by Njol and cannot function without it.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		instance = this;

		ConfigManager.load(this);

		boolean versionMatch;

		boolean register = false;
		boolean oldVersion = false;
		String APIversion = "1.4.0-SNAPSHOT"; // Interal API Merchants version
		/*
		 * returns true if you have the plugin Merchants and false if not
		 */
		boolean hasMerchants = Bukkit.getPluginManager().getPlugin("Merchants") != null;

		/*
		 * if you're using an older version of the API and the server runs on an internally supported version then use the internal version.
		 */
		if (hasMerchants)
		{
			String externalVer = Bukkit.getPluginManager().getPlugin("Merchants").getDescription().getVersion();

			// all old versions found on GitHub.com/Cybermaxke/MerchantsAPI
			if ("1.3.0-SNAPSHOT".equals(externalVer) || "1.2.0-SNAPSHOT".equals(externalVer) || "1.2-SNAPSHOT".equals(externalVer)
					|| "1.1.1-SNAPSHOT".equals(externalVer) || "1.1.0-SNAPSHOT".equals(externalVer) || "1.0.1-SNAPSHOT".equals(externalVer)
					|| "1.0.0-SNAPSHOT".equals(externalVer))
			{
				//Only use internal version if the server runs a supported version
				if ("v18r3".equals(MCversion) || "v19r2".equals(MCversion) || "v110r1".equals(MCversion))
				{ //TODO add universal support, so it's easier to maintain
						//disable Merchants plugin
					Plugin plugin = Bukkit.getPluginManager().getPlugin("Merchants");
					getServer().getPluginManager().disablePlugin(plugin);
					oldVersion = true; //let the rest of the progam that the external version was old

					getLogger().info("Found plugins Merchants but its using an old version of the API (" + externalVer
							+ "). The plugin will be disabled and an internal, newer version (" + APIversion + "), will be used in it's place.");
				} else
				{
					getLogger().info("Found plugins Merchants but its using an old version of the API (" + externalVer + "). Consider updating it.");

				}
			}
		}

		if (!hasMerchants || oldVersion)
		{
			SMerchantPlugin SMerchantPlugin = new SMerchantPlugin(this);
			versionMatch = SMerchantPlugin.Enable();
			if (!hasMerchants && versionMatch)
			{
				getLogger().info(
						"Could not find plugin Merchants, using internal API version " + APIversion + " with minecraft server version " + MCversion);
			}
		} else
		{
			versionMatch = true;
			getLogger().info("Found Merchants, using it's version");
		}

		if (versionMatch)
		{
			Skript.registerAddon(this);
			if (Skript.isAcceptRegistrations())
			{
				register = Register.RegisterMerchants(); // enable Merchants effect
				try
				{
					ConfigManager.loadTraders();
				} catch (NullPointerException e)
				{
					getLogger().warning(
							"Could not load traders due to you possibly using an old version of the config file! to fix this simply delete the config.yml in /plugins/tradersk/");
					e.printStackTrace(); //get their attention
				}
			} else
			{
				Skript.error("Could not register effects as skript is not accepting registrations.");
				register = false;
			}
		}
		if (!register || !versionMatch)
		{
			this.getServer().getConsoleSender()
					.sendMessage("[" + this.getName() + "] " + ChatColor.RED
							+ "Could not enable the plugin! Due to no interal support for minecraft version " + MCversion
							+ "! Please use the plugin Merchants by cybermaxke if you want to use this addon.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else
		{
			getLogger().info("Merchants effects added!");
			getLogger().info("Enabled ~ Created by kh498");
			enabled = true;
		}
	}

	@Override
	public void onDisable()
	{
		if (enabled)
			ConfigManager.saveTraders(true);
	}
}
