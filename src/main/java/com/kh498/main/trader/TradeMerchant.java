package com.kh498.main.trader;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.inventory.ItemStack;

import com.kh498.main.ConfigManager;

public class TradeMerchant
{
	private String displayName;
	private String internalName;
	private List<ItemStack> trades;

	public TradeMerchant(String name, @Nullable ArrayList<ItemStack> traders)
	{
		this.internalName = name;
		this.setDisplayName(name);
		if (traders != null)
		{
			this.setTrades(traders);
		} else
		{
			this.setTrades(new ArrayList<ItemStack>());
		}
	}

	/**
	 * Save a trader <b>Note</b>: This function creates a section for it's merchant
	 * 
	 * @param conf
	 *            Where to save the trader
	 */
	public void saveMerchant(ConfigurationSection conf)
	{
		ConfigurationSection mainSection = ConfigManager.getSectionOrCreate(conf, internalName);
		if (mainSection.contains("Items")){
			mainSection.set("Items", null);
		}
		ConfigurationSection tradesSection = ConfigManager.getSectionOrCreate(mainSection, "Items");
		
		mainSection.set("DisplayName", displayName);
		
		for (int i = 0; i < trades.size(); i++)
		{
			tradesSection.set("Item " + i, trades.get(i));
		}
	}

	/**
	 * @return the name
	 */
	public String getInternalName()
	{
		return internalName;
	}

	/**
	 * @return the trades
	 */
	public List<ItemStack> getTrades()
	{
		return trades;
	}

	/**
	 * @param trades
	 *            the trades to set
	 */
	public void setTrades(List<ItemStack> trades)
	{
		this.trades = trades;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName)
	{
		checkNotNull(displayName);
		this.displayName = displayName;
	}
}
