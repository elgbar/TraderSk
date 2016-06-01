package com.kh498.main.trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.cybermaxke.merchants.api.Merchant;
import me.cybermaxke.merchants.api.MerchantAPI;
import me.cybermaxke.merchants.api.Merchants;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;

import com.kh498.main.Main;

public class Trader {
	private static Map<String, Object> Traders = new HashMap<String, Object>();
	private static boolean debug = false;

	private static Main instance;

	public Trader(Main instance) {
		Trader.instance = instance;
	}

	public static void TraderNew(String name) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		Traders.put(name, list);
	}

	public static void TraderRemove(String name) {
		if (Traders.containsKey(name)) {
			@SuppressWarnings("unchecked")
			List<ItemStack> list = (List<ItemStack>) Traders.get(name);

			if (list.isEmpty()) {
				return;
			}
			list.clear();
			instance.saveTraders(false);
			
			Traders.remove(name);
		} else {
			noTrader(name);
		}
	}

	public static void TraderRemovePage(String name, int page) {
		if (Traders.containsKey(name)) {
			@SuppressWarnings("unchecked")
			List<ItemStack> list = (List<ItemStack>) Traders.get(name);

			if (list.isEmpty()) {
				return;
			}

			int pages = getPages(list) - 1;

			if (0 > page) {
				Skript.error("The requested page number is too low, it cannot be lower than 0");
				return;
			} else if (page > pages) {
				Skript.error("The requested page number is too high, it cannot be higher than "
						+ pages);
				return;
			}
			// set the proper page (as there is 3 items per page)
			page = page * 3; 

			if (pages == 0) {
				list.clear();
				return;
			}
			if (debug) {
				Skript.error("Going to remove: " + list.get(page) + ", "
						+ list.get(page + 1) + " and " + list.get(page + 2));
				Skript.error("before: " + list);
			}
			// remove the item from array
			for (int i = 2; i >= 0; i--) {
				list.remove(page + i);
			}
			
			//save items to disk
			Traders.replace(name, list); // update the trader list
			instance.saveTraders(false);
			
			if (debug)
				Skript.error("after:  " + list);
		
		} else {
			noTrader(name);
		}
	}

	/**
	 * Sets a variable.
	 * 
	 * @param name
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
	public static void TraderSetPage(String name, int page, ItemStack item0,
			ItemStack item1, ItemStack item2) {
		// The trader exist
		if (Traders.containsKey(name)) {
			@SuppressWarnings("unchecked")
			List<ItemStack> list = (List<ItemStack>) Traders.get(name);
			page = page * 3; // set the proper page (as there is 3 items per
								// page)
			ItemStack itemIn = null; // item in if there is only one input item

			if (0 > page) {
				Skript.error("The requested page number is too low, it cannot be lower than 0");
				return;
			} else if (page != 0) {
				try {
					ItemStack itemX = list.get(page - 3);
					if (itemX == null) {
						return;
					}
				} catch (IndexOutOfBoundsException e) {
					Skript.error("Could not add items to page "
							+ (page / 3)
							+ " as there are no items in the previous page.");
					return;
				}
			}

			// Check if there is only one item in
			if (item1 == null && item2 != null) {
				itemIn = item2;
			} else if (item2 == null && item1 != null) {
				itemIn = item1;
			}

			if (item0 == null || (item1 == null && item2 == null)) {
				Skript.error(
						"Output item and at least one input item must exist",
						ErrorQuality.SEMANTIC_ERROR);
				return;
			}

			// Check if any of the items are invalid
			if (!isValidMaterial(item0, false)
					|| !isValidMaterial(item1, false)
					|| !isValidMaterial(item2, true)
					|| !isValidMaterial(itemIn, true)) {
				Skript.error("One of the items is a blacklisted item!",
						ErrorQuality.SEMANTIC_ERROR);
				return;
			}

			// try and set the outitem, if it throws an
			// IndexOutOfBoundsException add the item.
			String itemOUT, itemIN1, itemIN2, mode;
			try {
				itemOUT = "" + list.get(page);
				mode = "set";
			} catch (IndexOutOfBoundsException e) {
				itemOUT = "NONE";
				mode = "add";
			}
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
				System.out.println("itemOUT: " + itemOUT + " | itemIN1: "
						+ itemIN1 + " | itemIN2: " + itemIN2 + "\nitem0: "
						+ item0 + " item1: " + item1 + " item2: " + item2
						+ " itemIn: " + itemIn + "\nindex: " + page + " size: "
						+ list.size() + " pages: " + pages);

				Skript.error("itemOUT: " + itemOUT + "itemIN1: " + itemIN1
						+ "itemIN2: " + itemIN2 + " | index: " + page);
			}
			if (mode == "set") {
				list.set(page, item0); // set the output item
				if (debug)
					Skript.error("item0: set");
			} else {
				if (debug)
					Skript.error("item0: add");
				list.add(item0); // set the output item
				mode = "add";
			}
			if (itemIn != null) { // if there is only one item set it to the
									// first input slot, and delete the secound
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
			Traders.replace(name, list); // update the trader list
			instance.saveTraders(false);
		} else {
			noTrader(name);
		}
	}

	public static void TraderListPages(String name, Player player) {
		if (Traders.containsKey(name)) {
			@SuppressWarnings("unchecked")
			List<ItemStack> list = (List<ItemStack>) Traders.get(name); // list
																		// over
																		// all
			// items for this
			// trader
			int pages = getPages(list);
			String ShowItem0, ShowItem1, ShowItem2;
			ShowItem0 = ShowItem1 = ShowItem2 = "Empty";
			ItemStack item0, item1, item2;
			player.sendMessage(ChatColor.GOLD + "Trader " + name.toLowerCase()
					+ "'s items:");
			player.sendMessage(ChatColor.GRAY + "There are " + pages
					+ " pages.");
			for (int i = 0; i < pages; i++) {
				int page = i * 3;
				item0 = list.get(page);
				item1 = list.get(page + 1);
				item2 = list.get(page + 2);
				if (isValidMaterial(item0, false)) {
					ShowItem0 = "" + item0.getType();
				}
				if (isValidMaterial(item1, false)) {
					ShowItem1 = "" + item1.getType();
				}
				if (isValidMaterial(item2, true)) {
					ShowItem2 = "" + item2.getType();
				}
				player.sendMessage(ChatColor.YELLOW + "" + i + ": " + ShowItem0
						+ ", " + ShowItem1 + " and " + ShowItem2);
			}
		} else {
			noTrader(name);
		}
	}

	public static void TraderOpen(String name, Player player) {
		// The trader exist
		if (Traders.containsKey(name)) {
			@SuppressWarnings("unchecked")
			// list over all items for this trader
			List<ItemStack> list = (List<ItemStack>) Traders.get(name);

			// if there is no items, do not open the trader
			if (list.size() == 0) {
				return;
			}

			ItemStack item0, item1, item2;
			int page;
			int pages = getPages(list);

			if (debug)
				Skript.error("" + pages + " | " + list.size());

			// create merchant
			MerchantAPI api = Merchants.get();
			Merchant merchant;
			try {
				merchant = api.newMerchant("");
			} catch (NullPointerException e) {
				Skript.error("Could create merchant as it did not exist.\n" + e);
				return;
			}

			for (int i = 0; i < pages; i++) {
				page = i * 3;
				item0 = list.get(page);
				item1 = list.get(page + 1);
				try {
					item2 = list.get(page + 2);
				} catch (IndexOutOfBoundsException e) {
					item2 = null;
				}
				if (isValidMaterial(item2, true)) { // item2 is air(aka does not
													// exist)
					merchant.addOffer(api.newOffer(item0, item1, item2));
				} else if (isValidMaterial(item0, false)
						&& isValidMaterial(item1, false)) {
					merchant.addOffer(api.newOffer(item0, item1));
				} else {
					Skript.error(
							"Could not add offer as the item either was illegal or does not exist.",
							ErrorQuality.SEMANTIC_ERROR);
				}
			}
			merchant.addCustomer(player);
		} else {
			noTrader(name);
		}
	}

	// item is the item checked canBeNull is wherever the item can be null
	@SuppressWarnings("deprecation")
	private static boolean isValidMaterial(ItemStack item, boolean canBeNull) {
		if (item != null) {
			// below is list illegal materials
			if (
			// item.getTypeId() == || //
			item.getTypeId() == 115
					|| // nether wart block
					item.getTypeId() == 36
					|| // moved block (?)

					// item.getType().equals(Material.getMaterial("")) ||
					item.getType().equals(
							Material.getMaterial("DOUBLE_STONE_SLAB"))
					|| item.getType().equals(
							Material.getMaterial("REDSTONE_COMPARATOR_ON"))
					|| item.getType().equals(
							Material.getMaterial("REDSTONE_COMPARATOR_OFF"))
					|| item.getType()
							.equals(Material.getMaterial("FLOWER_POT"))
					|| item.getType().equals(Material.getMaterial("TRIPWIRE"))
					|| item.getType().equals(
							Material.getMaterial("REDSTONE_LAMP_ON"))
					|| item.getType().equals(Material.getMaterial("POTATO"))
					|| item.getType().equals(Material.getMaterial("CARROT"))
					|| item.getType().equals(Material.getMaterial("CAULDRON"))
					|| item.getType().equals(
							Material.getMaterial("REWING_STAND"))
					|| item.getType().equals(Material.getMaterial("SKULL"))
					|| item.getType().equals(
							Material.getMaterial("WOODEN_DOOR"))
					|| item.getType().equals(
							Material.getMaterial("SPRUCE_DOOR"))
					|| item.getType().equals(
							Material.getMaterial("JUNGLE_DOOR"))
					|| item.getType().equals(
							Material.getMaterial("DARK_OAK_DOOR"))
					|| item.getType().equals(
							Material.getMaterial("ACACIA_DOOR"))
					|| item.getType()
							.equals(Material.getMaterial("BIRCH_DOOR"))
					|| item.getType().equals(
							Material.getMaterial("DIODE_BLOCK_ON"))
					|| item.getType().equals(
							Material.getMaterial("DIODE_BLOCK_OFF"))
					|| item.getType()
							.equals(Material.getMaterial("CAKE_BLOCK"))
					|| item.getType().equals(
							Material.getMaterial("SUGAR_CANE_BLOCK"))
					|| item.getType().equals(
							Material.getMaterial("IRON_DOOR_BLOCK"))
					|| item.getType()
							.equals(Material.getMaterial("SIGN_POST "))
					|| item.getType().equals(Material.getMaterial("WALL_SIGN"))
					|| item.getType().equals(
							Material.getMaterial("REDSTONE_WIRE"))
					|| item.getType().equals(Material.getMaterial("COCOA"))
					|| item.getType().equals(Material.getMaterial("AIR"))
					|| item.getType().equals(Material.getMaterial("WATER"))
					|| item.getType().equals(
							Material.getMaterial("STATIONARY_WATER"))
					|| item.getType().equals(Material.getMaterial("LAVA"))
					|| item.getType().equals(
							Material.getMaterial("STATIONARY_LAVA"))
					|| item.getType().equals(Material.getMaterial("PORTAL"))
					|| item.getType().equals(
							Material.getMaterial("ENDER_PORTAL "))
					|| item.getType().equals(
							Material.getMaterial("PISTON_EXTENSION"))
					|| item.getType().equals(
							Material.getMaterial("PISTON_MOVING_PIECE"))
					|| item.getType().equals(Material.getMaterial("BED_BLOCK"))
					|| item.getType()
							.equals(Material.getMaterial("MELON_STEM"))
					|| item.getType().equals(Material.getMaterial("FIRE"))) {
				return false;
			} else {
				return true;
			}
		} else {
			if (canBeNull) {
				return true;
			} else {
				return false;
			}
		}
	}
	// get number of pages in for a trader
	private static int getPages(List<ItemStack> list) { 
		return Math.floorDiv(list.size() + 1, 3);
	}

	private static void noTrader(String name) {
		Skript.error("No trader found named " + name,
				ErrorQuality.SEMANTIC_ERROR);
	}

	public static Map<String, Object> getTrader() {
		return Traders;
	}

	public static void setTrader(Map<String, Object> map) {
		Traders = map;
	}
}
