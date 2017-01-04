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

package com.kh498.main.trader;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kh498.main.MainConfigManager;
import com.kh498.main.TraderConfigManager;
import com.kh498.main.util.Util;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;
import me.cybermaxke.merchants.api.Merchant;
import me.cybermaxke.merchants.api.MerchantAPI;
import me.cybermaxke.merchants.api.Merchants;

public class Trader
{
	private static Map<String, TradeMerchant> traders = new HashMap<String, TradeMerchant> ();

	public static TradeMerchant getTradeMerchant (String name)
	{
		return (traders.get (name) != null) ? traders.get (name) : null;
	}

	/**
	 * @param trader
	 *            Name of trader
	 */
	public static void TraderNew (String traderName)
	{
		traders.put (traderName, new TradeMerchant (traderName, null));
	}

	/**
	 * @param trader
	 *            Name of trader
	 * @param name
	 *            Display name of trader
	 */

	public static void TraderSetTitle (TradeMerchant trader, String name)
	{
		checkNotNull (name, "name");
		checkNotNull (trader);
		//Don't change the name if it is the same
		String oldName = trader.getDisplayName ();
		if (oldName != null)
		{
			if (oldName.equals (name))
			{
				return;
			}
		}

		trader.setDisplayName (Util.getJSON (name));
		// Save trades to disk
	}

	/**
	 * Removes a trader
	 * 
	 * @param trader
	 *            Name of trader
	 */
	public static void TraderRemove (TradeMerchant trader)
	{
		checkNotNull (trader);
		if (traders.containsKey (trader.getInternalName ()))
		{
			TraderConfigManager.removeTrader (trader);
			traders.remove (trader.getInternalName ());
		}
	}

	/**
	 * Removes all traders from memory and disk.
	 */
	public static void TraderRemoveAll ()
	{
		traders.clear (); //clear trader list from memory
	}

	/**
	 * Removes a page from a trader
	 * 
	 * @param trader
	 *            Name of trader
	 * @param page
	 *            What page to remove
	 */
	public static void TraderRemovePage (TradeMerchant trader, int page)
	{
		checkNotNull (trader);
		List<ItemStack> list = trader.getTrades ();

		if (list.isEmpty ())
		{
			return;
		} // return if there is nothing to remove

		int pages = Util.getPages (list);
		//TODO >= & below 1
		if (0 > page)
		{ // there is not page below 0
			Skript.error ("The requested page number is too low, it cannot be lower than 0");
			return;
		} else if (page >= pages)
		{ // Requested page to remove is higher
				// than nr of pages
			Skript.error ("The requested page number is too high, it cannot be higher than " + pages);
			return;
		}
		// set the proper page (as there is 3 items per page)
		page = page * 3;

		if (pages <= 0)
		{ // clear the list if there are no pages
			list.clear ();
			return;
		}
		if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
		{
			Skript.error ("Going to remove: " + list.get (page) + ", " + list.get (page + 1) + " and " + list.get (page + 2));
			Skript.error ("before: " + list);
		}
		// remove the item from array
		for (int i = 2; i >= 0; i--)
		{
			list.remove (page + i);
		}

		// save items to disk
		trader.setTrades (list);

		if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
			Skript.error ("after:  " + list);

	}

	/**
	 * Add or modify a page for a trader
	 * 
	 * @param trader
	 *            The name of the trader
	 * @param page
	 *            What page to modify
	 * @param item0
	 *            Item set in the out slot, cannot be null
	 * @param item1
	 *            Item set in the first in slot, cannot be null
	 * @param item2
	 *            Item set in the secound in slot, can be null
	 */
	public static void TraderSetPage (TradeMerchant trader, int page, ItemStack item0, ItemStack item1, @ Nullable ItemStack item2)
	{
		checkNotNull (trader);
		//get the list of traders
		List<ItemStack> list = trader.getTrades ();

		// Only accept pages that are higher or equal to 0
		if (0 > page)
		{
			Skript.error ("The requested page number is too low, it cannot be lower than 0");
			return;

		} else if (page != 0)
		{
			try
			{
				list.get (page - 1); // this is just a test to check if there
									// is a page previous to the requested
									// page
			} catch (IndexOutOfBoundsException e)
			{
				Skript.error ("Could not add items to page " + page + " as there are no items in the previous page.");
				return;
			}
		}

		page = page * 3; // set the proper page (as there is 3 items per
						// page)
		ItemStack itemIn = null; // item in if there is only one input item

		// If one of the items is null set itemIn to the other one
		if (item1 == null && item2 != null)
		{
			itemIn = item2;
		} else if (item2 == null && item1 != null)
		{
			itemIn = item1;
		}

		// Check if any of the items are invalid
		if (!Util.isValidMaterial (item0, false) || !Util.isValidMaterial (item1, false) || !Util.isValidMaterial (item2, true)
				|| !Util.isValidMaterial (itemIn, true))
		{
			if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
				Skript.error ("Invalid material");
			return;
		}

		/*
		 * There are two ways to modify the list one is 'add' and the other one is 'set'. If the mode is 'set' then an existing page will be edited.
		 * If mode is 'add' then a new page will be added to the trader
		 */

		String itemOUT, itemIN1, itemIN2, mode;

		/*
		 * Try and set the outitem, if it throws an IndexOutOfBoundsException add the item.
		 */
		try
		{
			itemOUT = "" + list.get (page);
			mode = "set";
		} catch (IndexOutOfBoundsException e)
		{
			itemOUT = "NONE";
			mode = "add";
		}

		// debug output on the trade
		if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
		{
			int pages = Util.getPages (list);
			try
			{
				itemIN1 = "" + list.get (page + 1);
			} catch (IndexOutOfBoundsException e)
			{
				itemIN1 = "NONE";
			}
			try
			{
				itemIN2 = "" + list.get (page + 2);
			} catch (IndexOutOfBoundsException e)
			{
				itemIN2 = "NONE";
			}
			System.out.println ("itemOUT: " + itemOUT + " | itemIN1: " + itemIN1 + " | itemIN2: " + itemIN2 + "\nitem0: " + item0 + " item1: " + item1
					+ " item2: " + item2 + " itemIn: " + itemIn + "\nindex: " + page + " size: " + list.size () + " pages: " + pages);

			Skript.error ("itemOUT: " + itemOUT + "itemIN1: " + itemIN1 + "itemIN2: " + itemIN2 + " | index: " + page);
		}
		if (mode == "set")
		{
			list.set (page, item0); // set the output item
			if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
				Skript.error ("item0: set");
		} else
		{ // mode is add
			if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
				Skript.error ("item0: add");
			list.add (item0); // set the output item

		}
		/*
		 * ItemIn will only not be null when there is only one item as input, therefore the second item is set to air
		 */

		if (itemIn != null)
		{
			ItemStack air = new ItemStack (Material.AIR);
			if (mode == "set")
			{
				if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
					Skript.error ("itemIn: set");
				list.set (page + 1, itemIn);
				list.set (page + 2, air); // removes item
			} else
			{
				if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
					Skript.error ("itemIn: add");
				list.add (itemIn);
				list.add (air); // removes item

			}
		} else
		{
			if (mode == "set")
			{
				if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
					Skript.error ("item1: set");
				list.set (page + 1, item1);
				list.set (page + 2, item2);
			} else
			{
				if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
					Skript.error ("item2: add");
				list.add (item1);
				list.add (item2);
			}
		}
		trader.setTrades (list);
	}

	/**
	 * <p>
	 * List all trades from memory and shows it to a player
	 * </p>
	 * <p>
	 * <strong>Note:</strong> This is a debug effect and should not be in an realeased version
	 * </p>
	 * 
	 * @param trader
	 *            Name of trader
	 * @param player
	 *            Player to send text to
	 */
	public static void TraderListPages (TradeMerchant trader, Player player)
	{
		checkNotNull (trader);

		List<ItemStack> list = trader.getTrades ();
		int pages = Util.getPages (list);
		String ShowItem0, ShowItem1, ShowItem2;
		ShowItem0 = ShowItem1 = ShowItem2 = "empty";
		ItemStack item0, item1, item2;
		player.sendMessage (ChatColor.GOLD + "Trader " + trader.getDisplayName () + "'s (internal name: '" + trader.getInternalName () + "') items");
		player.sendMessage (ChatColor.GRAY + "There are " + pages + " page(s):");
		for (int i = 0; i < pages; i++)
		{
			int page = i * 3;
			item0 = list.get (page);
			item1 = list.get (page + 1);
			item2 = list.get (page + 2);
			if (Util.isValidMaterial (item0, false))
			{
				ShowItem0 = item0.getType ().toString ().toLowerCase ();
			}
			if (Util.isValidMaterial (item1, false))
			{
				ShowItem1 = item1.getType ().toString ().toLowerCase ();
			}
			if (Util.isValidMaterial (item2, true))
			{
				ShowItem2 = item2.getType ().toString ().toLowerCase ();
			}
			player.sendMessage (ChatColor.YELLOW + "" + i + ": " + ShowItem0 + ", " + ShowItem1 + " and " + ShowItem2);
		}
	}

	/**
	 * Open a merchant inventory to a player
	 * 
	 * @param trader
	 *            Name of trader
	 * @param player
	 *            Player to open the merchant to
	 */
	public static void TraderOpen (TradeMerchant trader, Player player)
	{
		// The trader exist
		checkNotNull (trader);

		// list over all items for this trader
		List<ItemStack> list = trader.getTrades ();

		// if there is no items, do not open the trader
		if (list.size () == 0)
		{
			if (!MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.OPEN_EMPTY_PATH))
				return;
		}

		ItemStack item0, item1, item2;
		int page;
		int pages = Util.getPages (list);

		if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
			Skript.error ("" + pages + " | " + list.size ());

		// create merchant
		MerchantAPI api = Merchants.get ();
		Merchant merchant;
		try
		{
			merchant = api.newMerchant (trader.getDisplayName ());
			merchant.setTitle (trader.getDisplayName (), true);
		} catch (NullPointerException e)
		{
			Skript.exception (e, "Could not open merchant as the api is not enabled.");
			//e.printStackTrace();
			return;
		}
		//first page starts at 0 therefore i == 0
		for (int i = 0; i < pages; i++)
		{
			page = i * 3;
			item0 = list.get (page);
			item1 = list.get (page + 1);
			try
			{
				item2 = list.get (page + 2);
			} catch (IndexOutOfBoundsException e)
			{
				item2 = null;
			}
			/*
			 * If the second 'in' item is empty it is stored as air, here it is converted back to nothing again
			 */
			if (item2.getType ().equals (Material.getMaterial ("AIR")))
			{
				item2 = null;
			}
			if (Util.isValidMaterial (item2, true))
			{
				merchant.addOffer (api.newOffer (item0, item1, item2));
			} else if (Util.isValidMaterial (item0, false) && Util.isValidMaterial (item1, false))
			{
				merchant.addOffer (api.newOffer (item0, item1));
			} else
			{
				Skript.error ("Could not add offer as the item either was illegal or does not exist.", ErrorQuality.SEMANTIC_ERROR);
			}
		}
		merchant.addCustomer (player);
	}

	/**
	 * @return the traders
	 */
	public static Map<String, TradeMerchant> getTraders ()
	{
		return traders;
	}

	/**
	 * @param traders
	 *            the traders to set
	 */
	public static void setTraders (Map<String, TradeMerchant> newTraders)
	{
		traders = newTraders;
	}
}
