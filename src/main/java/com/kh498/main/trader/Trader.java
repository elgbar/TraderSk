/*
 * This file is part of TraderSk
 *
 * Copyright (C) kh498
 *
 * TraderSk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraderSk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TraderSk.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kh498.main.trader;

import ch.njol.skript.Skript;
import ch.njol.skript.log.ErrorQuality;
import com.kh498.main.Main;
import com.kh498.main.MainConfigManager;
import com.kh498.main.TraderConfigManager;
import com.kh498.main.util.Util;
import me.cybermaxke.merchants.api.Merchant;
import me.cybermaxke.merchants.api.MerchantAPI;
import me.cybermaxke.merchants.api.Merchants;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class Trader {
    private static Map<String, TradeMerchant> traders = new HashMap<>();

    public static TradeMerchant getTradeMerchant(final String name) {
        return traders.get(name);
    }

    /**
     * @param traderName Name of trader
     */
    public static void TraderNew(final String traderName) {
        traders.put(traderName, new TradeMerchant(traderName));
    }

    /**
     * @param trader Name of trader
     * @param name   Display name of trader
     */

    public static void TraderSetTitle(final TradeMerchant trader, final String name) {
        checkNotNull(name, "name");
        checkNotNull(trader);

        final String oldName = trader.getDisplayName();
        final String newName = Util.toJSON(name);

        Main.log("new JSON name: " + newName);
        if (oldName != null && oldName.equals(newName)) {
            Main.log("");
            return;
        }

        trader.setDisplayName(newName);
    }

    /**
     * Removes a trader
     *
     * @param trader Name of trader
     */
    public static void TraderRemove(final TradeMerchant trader) {
        checkNotNull(trader);
        if (traders.containsKey(trader.getInternalName())) {
            TraderConfigManager.removeTrader(trader);
            traders.remove(trader.getInternalName());
        }
    }

    /**
     * Removes all traders from memory and disk.
     */
    public static void TraderRemoveAll() {
        traders.clear(); //clear trader list from memory
    }

    /**
     * Removes a page from a trader
     *
     * @param trader Name of trader
     * @param page   What page to remove
     */
    public static void TraderRemovePage(final TradeMerchant trader, int page) {
        checkNotNull(trader);
        final List<ItemStack> list = trader.getTrades();

        if (list.isEmpty()) {
            return;
        } // return if there is nothing to remove

        final int pages = Util.getPages(list);
        //TODO >= & below 1
        if (0 > page) { // there is not page below 0
            Skript.error("The requested page number is too low, it cannot be lower than 0");
            return;
        }
        else if (page >= pages) { // Requested page to remove is higher
            // than nr of pages
            Skript.error("The requested page number is too high, it cannot be higher than " + pages);
            return;
        }
        // set the proper page (as there is 3 items per page)
        page *= 3;

        if (pages <= 0) { // clear the list if there are no pages
            list.clear();
            return;
        }
        Main.log("Going to remove: " + list.get(page) + ", " + list.get(page + 1) + " and " + list.get(page + 2));
        Main.log("before removing: " + list);

        // remove the item from array
        for (int i = 2; i >= 0; i--) {
            list.remove(page + i);
        }

        // save items to disk
        trader.setTrades(list);

        Main.log("after removing: " + list);


    }

    /**
     * Add or modify a page for a trader
     *
     * @param trader     The name of the trader
     * @param page       What page to modify
     * @param outputItem Item set in the out slot, cannot be null
     * @param inputItem1 Item set in the first in slot, cannot be null
     * @param inputItem2 Item set in the second in slot, can be null
     */
    public static void TraderSetPage(final TradeMerchant trader, int page, final ItemStack outputItem,
                                     final ItemStack inputItem1, @Nullable final ItemStack inputItem2) {
        checkNotNull(trader);
        //get the list of traders
        final List<ItemStack> tradesList = trader.getTrades();

        page *= 3; // set the proper page (as there is 3 items per

        // Only accept pages that are higher or equal to 0
        if (page < 0) {
            Skript.error("The requested page number is too low, it cannot be lower than 0");
            return;
        }
        else if (tradesList.size() < page) {
            try {
                tradesList.get(page - 1); // this is just a test to check if there
                // is a page previous to the requested
                // page
            } catch (final IndexOutOfBoundsException e) {
                Skript
                    .error("Could not add items to page " + page / 3 + " as there are no items in the previous page.");
                return;
            }
            Skript.error("Could not add items to page " + page / 3 + " as there are no items in the previous page.");
            return;
        }

        // page)
        ItemStack itemIn = null; // item in if there is only one input item

        // If one of the items is null set itemIn to the other one
        if (inputItem1 == null && inputItem2 != null) {
            itemIn = inputItem2;
        }
        else if (inputItem2 == null && inputItem1 != null) {
            itemIn = inputItem1;
        }

        // Check if any of the items are invalid
        if (!Util.isValidMaterial(outputItem, false) || !Util.isValidMaterial(inputItem1, false) ||
            !Util.isValidMaterial(inputItem2, true) || !Util.isValidMaterial(itemIn, true)) {
            Main.log("Invalid material");

            return;
        }

		/*
         * There are two ways to modify the list one is 'add' and the other one is 'set'. If the mode is 'set' then an existing page will be edited.
		 * If mode is 'add' then a new page will be added to the trader
		 */

        String itemOUT, itemIN1, itemIN2, mode;

		/*
         * Try and set the out item, if it throws an IndexOutOfBoundsException add the item.
		 */
        try {
            itemOUT = "" + tradesList.get(page);
            mode = "set";
        } catch (final IndexOutOfBoundsException e) {
            itemOUT = "NONE";
            mode = "add";
        }

        // debug output on the trade
        if (MainConfigManager.getMainConfig().getBoolean(MainConfigManager.DEBUG_PATH)) {
            final int pages = Util.getPages(tradesList);
            try {
                itemIN1 = "" + tradesList.get(page + 1);
            } catch (final IndexOutOfBoundsException e) {
                itemIN1 = "NONE";
            }
            try {
                itemIN2 = "" + tradesList.get(page + 2);
            } catch (final IndexOutOfBoundsException e) {
                itemIN2 = "NONE";
            }
            Main.log("itemOUT: " + itemOUT + " | itemIN1: " + itemIN1 + " | itemIN2: " + itemIN2 + "\noutputItem: " +
                     outputItem + " inputItem1: " + inputItem1 + " inputItem2: " + inputItem2 + " itemIn: " + itemIn +
                     "\nindex: " + page + " size: " + tradesList.size() + " pages: " + pages);

            Main.log("itemOUT: " + itemOUT + "itemIN1: " + itemIN1 + "itemIN2: " + itemIN2 + " | index: " + page);
        }
        if ("set".equals(mode)) {
            tradesList.set(page, outputItem); // set the output item
            Main.log("outputItem: set");

        }
        else { // mode is add
            Main.log("outputItem: add");

            tradesList.add(outputItem); // set the output item

        }
        /*
         * ItemIn will only not be null when there is only one item as input, therefore the second item is set to air
		 */

        if (itemIn != null) {
            final ItemStack air = new ItemStack(Material.AIR);
            if ("set".equals(mode)) {
                Main.log("itemIn: set");

                tradesList.set(page + 1, itemIn);
                tradesList.set(page + 2, air); // removes item
            }
            else {
                Main.log("itemIn: add");

                tradesList.add(itemIn);
                tradesList.add(air); // removes item

            }
        }
        else {
            if ("set".equals(mode)) {
                Main.log("inputItem1: set");

                tradesList.set(page + 1, inputItem1);
                tradesList.set(page + 2, inputItem2);
            }
            else {
                Main.log("inputItem2: add");


                tradesList.add(inputItem1);
                tradesList.add(inputItem2);
            }
        }
        trader.setTrades(tradesList);
    }

    /**
     * <p>
     * List all trades from memory and shows it to a player
     * </p>
     * <p>
     * <strong>Note:</strong> This is a debug effect and should not be in an released version
     * </p>
     *
     * @param trader Name of trader
     * @param player Player to send text to
     */
    public static void TraderListPages(final TradeMerchant trader, final Player player) {
        checkNotNull(trader);

        final List<ItemStack> list = trader.getTrades();
        final int pages = Util.getPages(list);
        String ShowOutputItem, ShowInputItem1, ShowInputItem2;
        ShowOutputItem = ShowInputItem1 = ShowInputItem2 = "empty";
        ItemStack outputItem, inputItem1, inputItem2;
        player.sendMessage(
            ChatColor.GOLD + "Trader " + trader.getDisplayName() + "'s (internal name: '" + trader.getInternalName() +
            "') items");
        player.sendMessage(ChatColor.GRAY + "There are " + pages + " page(s):");
        for (int pageNr = 0; pageNr < pages; pageNr++) {
            final int page = pageNr * 3;
            outputItem = list.get(page);
            inputItem1 = list.get(page + 1);
            inputItem2 = list.get(page + 2);
            if (Util.isValidMaterial(outputItem, false)) {
                ShowOutputItem = outputItem.getType().toString().toLowerCase();
            }
            if (Util.isValidMaterial(inputItem1, false)) {
                ShowInputItem1 = inputItem1.getType().toString().toLowerCase();
            }
            if (Util.isValidMaterial(inputItem2, true)) {
                ShowInputItem2 = inputItem2.getType().toString().toLowerCase();
            }
            player.sendMessage(
                ChatColor.YELLOW + "" + pageNr + ": " + ShowOutputItem + ", " + ShowInputItem1 + " and " +
                ShowInputItem2);
        }
    }

    /**
     * Open a merchant inventory to a player
     *
     * @param trader Name of trader
     * @param player Player to open the merchant to
     */
    public static void TraderOpen(final TradeMerchant trader, final Player player) {
        // The trader exist
        checkNotNull(trader);
        if (player == null) {
            return;
        }

        // list over all items for this trader
        final List<ItemStack> tradesList = trader.getTrades();

        // if there is no items, do not open the trader
        if (tradesList.size() == 0) {
            if (!MainConfigManager.getMainConfig().getBoolean(MainConfigManager.OPEN_EMPTY_PATH)) { return; }
        }

        ItemStack outputItem, inputItem1, inputItem2;
        int page;
        final int pages = Util.getPages(tradesList);

        Main.log("Nr of pages: " + pages + " | Size of item list: " + tradesList.size());


        // create merchant
        final MerchantAPI api = Merchants.get();
        final Merchant merchant;
        try {
            Main.log("Raw Merchant name: " + trader.getDisplayName());
            merchant = api.newMerchant(trader.getDisplayName(), true);

        } catch (final NullPointerException e) {
            throw Skript.exception(e, "Could not open merchant as the api is not enabled.");
        }
        //first page starts at 0 therefore i == 0
        for (int i = 0; i < pages; i++) {
            page = i * 3;
            outputItem = tradesList.get(page);
            inputItem1 = tradesList.get(page + 1);
            try {
                inputItem2 = tradesList.get(page + 2);
            } catch (final IndexOutOfBoundsException e) {
                inputItem2 = null;
            }
            /*
             * If the second 'in' item is empty it is stored as air, here it is converted back to nothing again
			 */
            if (inputItem2 == null || inputItem2.getType().equals(Material.AIR)) {
                inputItem2 = null;
            }
            if (Util.isValidMaterial(inputItem2, true)) {
                merchant.addOffer(api.newOffer(outputItem, inputItem1, inputItem2));
            }
            else if (Util.isValidMaterial(outputItem, false) && Util.isValidMaterial(inputItem1, false)) {
                merchant.addOffer(api.newOffer(outputItem, inputItem1));
            }
            else {
                Skript.error("Could not add offer as the item either was illegal or does not exist.",
                             ErrorQuality.SEMANTIC_ERROR);
            }
        }
        merchant.addCustomer(player);
    }

    /**
     * @return the traders
     */
    public static Map<String, TradeMerchant> getTraders() {
        return traders;
    }

    /**
     * @param newTraders the traders to set
     */
    public static void setTraders(final Map<String, TradeMerchant> newTraders) {
        traders = newTraders;
    }
}
