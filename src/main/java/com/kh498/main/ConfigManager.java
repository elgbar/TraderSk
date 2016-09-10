package com.kh498.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.kh498.main.trader.TradeMerchant;
import com.kh498.main.trader.Trader;

public class ConfigManager
{
	private final static String TRADERS_CONF_SEC = "Traders";

	private static YamlConfiguration mainConfig = null;
	private static File mainConfigFile = null;
	private static Plugin plugin = null;
	private static String CHAT_PREFIX;

	public static void load(Plugin p)
	{
		plugin = p;
		CHAT_PREFIX = "[" + p.getName() + "] ";
		mainConfigFile = new File(p.getDataFolder().getAbsolutePath());
		if (!mainConfigFile.exists() || !mainConfigFile.isDirectory())
			mainConfigFile.mkdir();
		mainConfigFile = new File(p.getDataFolder().getAbsolutePath(), "Traders.yml");
		try
		{
			if (!mainConfigFile.exists())
				mainConfigFile.createNewFile();
			mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Save traders from memory to disk
	 * 
	 * @param log
	 *            Boolean to determinte wherever to log info or not
	 */
	public static void saveTraders(final boolean log)
	{
		Map<String, TradeMerchant> traders = Trader.getTraders();
		if (traders.size() != 0)
		{
			try
			{
				ConfigurationSection configSec = getSectionOrCreate(mainConfig, TRADERS_CONF_SEC);
				for (TradeMerchant tm : traders.values())
				{
					tm.saveMerchant(configSec);
				}

				if (log)
					plugin.getLogger().info("Traders saved");

			} catch (Exception e)
			{
				e.printStackTrace();
				plugin.getLogger().warning("Could not create sections for traders");
			}
		} else
		{
			if (mainConfig.isSet(TRADERS_CONF_SEC))
			{
				mainConfig.set(TRADERS_CONF_SEC, null);
			}
		}
		try
		{
			saveMainConfig();
		} catch (NullPointerException e)
		{
			e.printStackTrace();
			plugin.getLogger().warning("Could not save traders");
		}
	}

	public static void removeTrader(TradeMerchant tm)
	{
		ConfigurationSection configSec = mainConfig.getConfigurationSection(TRADERS_CONF_SEC);
		if (configSec.contains(tm.getInternalName()))
			configSec.set(tm.getInternalName(), null);
	}

	public static ConfigurationSection getSectionOrCreate(ConfigurationSection parent, String sectionName)
	{
		ConfigurationSection section = parent.getConfigurationSection(sectionName);
		if (section == null)
			section = parent.createSection(sectionName);
		return section;
	}

	/**
	 * Load the traders from disk to memory
	 */
	public static void loadTraders()
	{
		ConfigurationSection mainSec = ConfigManager.getConfig().getConfigurationSection("Traders");
		Map<String, Object> map;
		try
		{
			map = mainSec.getValues(false);
		} catch (NullPointerException e)
		{
			Main.getInstance().getServer().getConsoleSender().sendMessage(CHAT_PREFIX + "Could not find any traders to load");
			return;
		}

		Map<String, TradeMerchant> newMap = new HashMap<String, TradeMerchant>();
		for (Map.Entry<String, Object> entry : map.entrySet())
		{
			TradeMerchant newTM = new TradeMerchant(entry.getKey(), null);
			ConfigurationSection traderSec = mainSec.getConfigurationSection(entry.getKey());

			if (traderSec.getString("DisplayName") != null)
			{
				newTM.setDisplayName(traderSec.getString("DisplayName"));
			}
			ConfigurationSection itemSec = traderSec.getConfigurationSection("Items");
			if (itemSec != null)
			{
				ArrayList<ItemStack> list = new ArrayList<ItemStack>();
				Boolean foundNext = true;
				int nr = 0;
				while (foundNext)
				{
					if (itemSec.isItemStack("Item " + nr))
					{
						list.add(itemSec.getItemStack("Item " + nr));
						nr++;
					} else
						foundNext = false;
				}
				newTM.setTrades(list);
			}
			newMap.put(entry.getKey(), newTM);
		}

		Trader.setTraders(newMap);
		Main.getInstance().getServer().getConsoleSender().sendMessage(CHAT_PREFIX + "Traders loaded");
	}

	public static YamlConfiguration getConfig()
	{
		return mainConfig;
	}

	private static void saveMainConfig()
	{
		if (mainConfig != null)
		{
			try
			{
				mainConfig.save(mainConfigFile);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
