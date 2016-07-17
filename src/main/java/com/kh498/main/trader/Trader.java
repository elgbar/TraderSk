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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.kh498.main.Main;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;
import me.cybermaxke.merchants.api.Merchant;
import me.cybermaxke.merchants.api.MerchantAPI;
import me.cybermaxke.merchants.api.Merchants;

public class Trader {
	private static Map<String, Object> Traders = new HashMap<String, Object>();
	private static Map<String, String> TradersName = new HashMap<String, String>();
	private static boolean debug = false;

	private static Main main;

	public Trader(Main instance) {
		Trader.main = instance;
	}

	/**
	 * 
	 * @param trader
	 *            Name of trader
	 */
	public static void TraderNew(String trader) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		TraderSetTitle(trader); // set the default name of merchant inventory
		
		Traders.put(trader, list);
	}

	/**
	 * Set the title of the trader to it's declaration name (default)
	 * 
	 * @param trader
	 *            Name of trader
	 */
	
	
	public static void TraderSetTitle(String trader) {
		TraderSetTitle(trader, trader);
	}
	
	/**
	 * 
	 * @param trader
	 *            Name of trader
	 * @param name
	 *            Display name of trader
	 */

	public static void TraderSetTitle(String trader, String name) {
		checkNotNull(name, "name");
		if (!validTrader(trader)) { return; } // return if the trader cannot be found
		
		//Don't change the name if it is the same
		String oldName = TradersName.get(trader);
//		System.out.println("New name: " + name);
//		System.out.println("Old name: " + oldName);
		if (oldName != null) {
			if (oldName.equals(name)) {
//				System.out.println("EQL old name");
				return; 
			}
		}
		// update the trader list
		try {
			TradersName.put(trader, name);
//			System.out.println("Replacing");
		} catch (NullPointerException e) {
			TradersName.replace(trader, name); 
//			System.out.println("Putting");
		}
		
		// Save trades to disk
		main.saveTraders(false);
	}
	 
	
	/**
	 * Removes a trader
	 * 
	 * @param trader
	 *            Name of trader
	 */
	@SuppressWarnings("unchecked")
	public static void TraderRemove(String trader) {
		if (!validTrader(trader)) { return; } // return if the trader cannot be found
		
		List<ItemStack> list = (List<ItemStack>) Traders.get(trader);

		if (list.isEmpty()) {
			return;
		}
		list.clear();
		Traders.remove(trader);
		main.saveTraders(false);
	}

	/**
	 * Removes all traders from memory and disk.
	 */
	public static void TraderRemoveAll() {
		Traders.clear(); //clear trader list from memory
		Map<String, Object> emptyMap = new HashMap<String, Object>(); //create empty list of traders
		main.getConfig().createSection("Traders", emptyMap); //overwrite with empty list

	}

	/**
	 * Removes a page from a trader
	 * 
	 * @param trader
	 *            Name of trader
	 * @param page
	 *            What page to remove
	 */
	@SuppressWarnings("unchecked")
	public static void TraderRemovePage(String trader, int page) {
		if (!validTrader(trader)) { return; } // return if the trader cannot be found

		List<ItemStack> list = (List<ItemStack>) Traders.get(trader);
		
		if (list.isEmpty()) { return; } // return if there is nothing to remove
		
		int pages = getPages(list); 
		//TODO >= & below 1
		if (0 > page) { // there is not page below 0
			Skript.error("The requested page number is too low, it cannot be lower than 0");
			return;
		} else if (page > pages) { // Requested page to remove is higher
									// than nr of pages
			Skript.error("The requested page number is too high, it cannot be higher than " + pages);
			return;
		}
		// set the proper page (as there is 3 items per page)
		page = page * 3;

		if (pages <= 0) { // clear the list if there are no pages
			list.clear();
			return;
		}
		if (debug) {
			Skript.error("Going to remove: " + list.get(page) + ", " + list.get(page + 1) + " and "
					+ list.get(page + 2));
			Skript.error("before: " + list);
		}
		// remove the item from array
		for (int i = 2; i >= 0; i--) {
			list.remove(page + i);
		}

		// save items to disk
		Traders.replace(trader, list); // update the trader list
		main.saveTraders(false);

		if (debug)
			Skript.error("after:  " + list);

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
	@SuppressWarnings("unchecked")
	public static void TraderSetPage(String trader, int page, ItemStack item0, ItemStack item1,
			@Nullable ItemStack item2) {
		// The trader exist
		if (!validTrader(trader)) { return; } // return if the trader cannot be found
		//get the list of traders
		List<ItemStack> list = (List<ItemStack>) Traders.get(trader);

		//TODO >= & to 1
		// Only accept pages that are higher or equal to 0
		if (0 > page) {
			Skript.error("The requested page number is too low, it cannot be lower than 0");
			return;
		//TODO only: } else {
		} else if (page != 0){
			try {
				list.get(page - 1); // this is just a test to check if there
									// is a page previous to the requested
									// page
			} catch (IndexOutOfBoundsException e) {
				Skript.error(
						"Could not add items to page " + page + " as there are no items in the previous page.");
				return;
			}
		}

		page = page * 3; // set the proper page (as there is 3 items per
							// page)
		ItemStack itemIn = null; // item in if there is only one input item

		// If one of the items is null set itemIn to the other one
		if (item1 == null && item2 != null) {
			itemIn = item2;
		} else if (item2 == null && item1 != null) {
			itemIn = item1;
		}

		// Check if any of the items are invalid
		if (!isValidMaterial(item0, false) || !isValidMaterial(item1, false) || !isValidMaterial(item2, true)
				|| !isValidMaterial(itemIn, true)) {
			return;
		}

		/*
		 * There are two ways to modify the list one is 'add' and the other
		 * one is 'set'. If the mode is 'set' then an existing page will be
		 * edited. If mode is 'add' then a new page will be added to the
		 * trader
		 */

		String itemOUT, itemIN1, itemIN2, mode;

		/*
		 * Try and set the outitem, if it throws an
		 * IndexOutOfBoundsException add the item.
		 */
		try {
			itemOUT = "" + list.get(page);
			mode = "set";
		} catch (IndexOutOfBoundsException e) {
			itemOUT = "NONE";
			mode = "add";
		}

		// debug output on the trade
		if (debug) {
			int pages = getPages(list);
			try {
				itemIN1 = "" + list.get(page + 1);
			} catch (IndexOutOfBoundsException e) {
				itemIN1 = "NONE";
			}
			try {
				itemIN2 = "" + list.get(page + 2);
			} catch (IndexOutOfBoundsException e) {
				itemIN2 = "NONE";
			}
			System.out.println("itemOUT: " + itemOUT + " | itemIN1: " + itemIN1 + " | itemIN2: " + itemIN2
					+ "\nitem0: " + item0 + " item1: " + item1 + " item2: " + item2 + " itemIn: " + itemIn
					+ "\nindex: " + page + " size: " + list.size() + " pages: " + pages);

			Skript.error(
					"itemOUT: " + itemOUT + "itemIN1: " + itemIN1 + "itemIN2: " + itemIN2 + " | index: " + page);
		}
		if (mode == "set") {
			list.set(page, item0); // set the output item
			if (debug)
				Skript.error("item0: set");
		} else { // mode is add
			if (debug)
				Skript.error("item0: add");
			list.add(item0); // set the output item

		}
		/*
		 * ItemIn will only not be null when there is only one item as
		 * input, therefore the second item is set to air
		 */

		if (itemIn != null) {
			ItemStack air = new ItemStack(Material.AIR);
			if (mode == "set") {
				if (debug)
					Skript.error("itemIn: set");
				list.set(page + 1, itemIn);
				list.set(page + 2, air); // removes item
			} else {
				if (debug)
					Skript.error("itemIn: add");
				list.add(itemIn);
				list.add(air); // removes item

			}
		} else {
			if (mode == "set") {
				if (debug)
					Skript.error("item1: set");
				list.set(page + 1, item1);
				list.set(page + 2, item2);
			} else {
				if (debug)
					Skript.error("item2: add");
				list.add(item1);
				list.add(item2);
			}
		}
		Traders.replace(trader, list); // update the trader list
		main.saveTraders(false);
	}

	/**
	 * <p>
	 * List all trades from memory and shows it to a player
	 * </p>
	 * 
	 * <p>
	 * <strong>Note:</strong> This is a debug effect and should not be in an
	 * realeased version
	 * </p>
	 * 
	 * @param trader
	 *            Name of trader
	 * @param player
	 *            Player to send text to
	 */
	@SuppressWarnings("unchecked")
	public static void TraderListPages(String trader, Player player) {
		if (!validTrader(trader)) { return; } // return if the trader cannot be found

		List<ItemStack> list = (List<ItemStack>) Traders.get(trader);
		int pages = getPages(list);
		String ShowItem0, ShowItem1, ShowItem2;
		ShowItem0 = ShowItem1 = ShowItem2 = "empty";
		ItemStack item0, item1, item2;
		player.sendMessage(ChatColor.GOLD + "Trader " + trader.toLowerCase() + "'s items");
		player.sendMessage(ChatColor.GRAY + "There are " + pages + " page(s):");
		for (int i = 0; i < pages; i++) {
			int page = i * 3;
			item0 = list.get(page);
			item1 = list.get(page + 1);
			item2 = list.get(page + 2);
			if (isValidMaterial(item0, false)) {
				ShowItem0 = item0.getType().toString().toLowerCase();
			}
			if (isValidMaterial(item1, false)) {
				ShowItem1 = item1.getType().toString().toLowerCase();
			}
			if (isValidMaterial(item2, true)) {
				ShowItem2 = item2.getType().toString().toLowerCase();
			}
			player.sendMessage(
					ChatColor.YELLOW + "" + i + ": " + ShowItem0 + ", " + ShowItem1 + " and " + ShowItem2);
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
	@SuppressWarnings("unchecked")
	public static void TraderOpen(String trader, Player player) {
		// The trader exist
		if (!validTrader(trader)) { return; } // return if the trader cannot be found

		// list over all items for this trader
		List<ItemStack> list = (List<ItemStack>) Traders.get(trader);

		// if there is no items, do not open the trader
		if (list.size() == 0) { return; }

		ItemStack item0, item1, item2;
		int page;
		int pages = getPages(list);

		if (debug)
			Skript.error("" + pages + " | " + list.size());

		// create merchant
		MerchantAPI api = Merchants.get();
		Merchant merchant;
		try {
			String title = TradersName.get(trader);
			merchant = api.newMerchant(title);
			//String name = getTitle(list); //get the title from the list
//			System.out.println(title);
			if (title != null)
				merchant.setTitle(title);
			else
				 // set the name of the inventory to the traders name
				merchant.setTitle(trader);
		} catch (NullPointerException e) {
			Skript.error("Could not open merchant as the api is not enabled.");
			e.printStackTrace();
			return;
		}
		//first page starts at 0 therefore i == 0
		for (int i = 0; i < pages; i++) {
			page = i * 3;
			item0 = list.get(page);
			item1 = list.get(page + 1);
			try {
				item2 = list.get(page + 2);
			} catch (IndexOutOfBoundsException e) {
				item2 = null;
			}
			/*
			 * If the second 'in' item is empty it is stored as air, here it
			 * is converted back to nothing again
			 */
			if (item2.getType().equals(Material.getMaterial("AIR"))) {
				item2 = null;
			}
			if (isValidMaterial(item2, true)) { // item2 is air(aka does not
												// exist)
				merchant.addOffer(api.newOffer(item0, item1, item2));
			} else if (isValidMaterial(item0, false) && isValidMaterial(item1, false)) {
				merchant.addOffer(api.newOffer(item0, item1));
			} else {
				Skript.error("Could not add offer as the item either was illegal or does not exist.",
						ErrorQuality.SEMANTIC_ERROR);
			}
		}
		merchant.addCustomer(player);
	}

	/**
	 * Check if a material can be used in a merchant inventory, if not it would
	 * crash the client.
	 * 
	 * @param item
	 *            itemstack that will be checked
	 * @param canBeNull
	 *            Wherever the itemstack can be null
	 * @return True if it is valid, false if not
	 */
	@SuppressWarnings("deprecation")
	private static boolean isValidMaterial(ItemStack item, boolean canBeNull) {
		if (item != null) {
			// below is list of illegal materials
			Material itemType = item.getType();
			if (
			// item.getTypeId() == || //
			item.getTypeId() == 115 || // nether wart block
					item.getTypeId() == 36 || // moved block (?)

					// item.getType().equals(Material.getMaterial("")) ||
					itemType.equals(Material.getMaterial("DOUBLE_STONE_SLAB"))
					|| itemType.equals(Material.getMaterial("REDSTONE_COMPARATOR_ON"))
					|| itemType.equals(Material.getMaterial("REDSTONE_COMPARATOR_OFF"))
					|| itemType.equals(Material.getMaterial("FLOWER_POT"))
					|| itemType.equals(Material.getMaterial("TRIPWIRE"))
					|| itemType.equals(Material.getMaterial("REDSTONE_LAMP_ON"))
					|| itemType.equals(Material.getMaterial("POTATO"))
					|| itemType.equals(Material.getMaterial("CARROT"))
					|| itemType.equals(Material.getMaterial("CAULDRON"))
					|| itemType.equals(Material.getMaterial("REWING_STAND"))
					|| itemType.equals(Material.getMaterial("SKULL"))
					|| itemType.equals(Material.getMaterial("WOODEN_DOOR"))
					|| itemType.equals(Material.getMaterial("SPRUCE_DOOR"))
					|| itemType.equals(Material.getMaterial("JUNGLE_DOOR"))
					|| itemType.equals(Material.getMaterial("DARK_OAK_DOOR"))
					|| itemType.equals(Material.getMaterial("ACACIA_DOOR"))
					|| itemType.equals(Material.getMaterial("BIRCH_DOOR"))
					|| itemType.equals(Material.getMaterial("DIODE_BLOCK_ON"))
					|| itemType.equals(Material.getMaterial("DIODE_BLOCK_OFF"))
					|| itemType.equals(Material.getMaterial("CAKE_BLOCK"))
					|| itemType.equals(Material.getMaterial("SUGAR_CANE_BLOCK"))
					|| itemType.equals(Material.getMaterial("IRON_DOOR_BLOCK"))
					|| itemType.equals(Material.getMaterial("SIGN_POST "))
					|| itemType.equals(Material.getMaterial("WALL_SIGN"))
					|| itemType.equals(Material.getMaterial("REDSTONE_WIRE"))
					|| itemType.equals(Material.getMaterial("COCOA"))
					|| itemType.equals(Material.getMaterial("AIR"))
					|| itemType.equals(Material.getMaterial("WATER"))
					|| itemType.equals(Material.getMaterial("STATIONARY_WATER"))
					|| itemType.equals(Material.getMaterial("LAVA"))
					|| itemType.equals(Material.getMaterial("STATIONARY_LAVA"))
					|| itemType.equals(Material.getMaterial("PORTAL"))
					|| itemType.equals(Material.getMaterial("ENDER_PORTAL"))
					|| itemType.equals(Material.getMaterial("PISTON_EXTENSION"))
					|| itemType.equals(Material.getMaterial("PISTON_MOVING_PIECE"))
					|| itemType.equals(Material.getMaterial("BED_BLOCK"))
					|| itemType.equals(Material.getMaterial("MELON_STEM"))
					|| itemType.equals(Material.getMaterial("FIRE"))) {
				Skript.error("Illegal material for one of the items '" + item.getType() + "'");
				return false;
			} else {
				return true;
			}
		} else {
			//return true if it can be null else return false
			return canBeNull; 
		}
	}

	/**
	 * Logs that no trader has been found with the name <tt>trader</tt>
	 * 
	 * @param trader
	 *            Name of trader
	 * @return 
	 * 			True if the trader can be found, else false
	 */
	private static boolean validTrader(String trader) {
		if (Traders.containsKey(trader)){
			return true;
		} else {
			Skript.error("No trader found named " + trader, ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
	}

	/**
	 * Get the correct number of pages for a trader, this is needed because in
	 * one page there are three items.
	 * 
	 * @param list
	 *            List of traders
	 * @return Number of pages
	 */
	private static int getPages(List<ItemStack> list) {
		return Math.floorDiv(list.size() + 1, 3); // + 1 since the list starts
													// at zero
	}
	/*
	private static String getTitle(List<ItemStack> list) {
		// index 0 is always the name of the trader
		ItemStack traderName = list.get(0);

		// set the title of the merchant inventory by default to the traders name
		ItemMeta newMeta = traderName.getItemMeta();
		return newMeta.getDisplayName();
	}
	*/
	public static Map<String, Object> getTrader() {
		return Traders;
	}

	public static void setTrader(Map<String, Object> map) {
		Traders = map;
	}

	public static Map<String, String> getTradersName() {
		return TradersName;
	}

	public static void setTradersName(Map<String, String> tradersName) {
		TradersName = tradersName;
	}
}
