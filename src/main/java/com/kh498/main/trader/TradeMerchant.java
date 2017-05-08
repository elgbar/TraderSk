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

import com.kh498.main.TraderConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeMerchant {
    private final String internalName;
    private String displayName;
    private List<ItemStack> trades;

    public TradeMerchant(final String name) {
        this.internalName = name;
        Trader.TraderSetTitle(this, name);
        this.setTrades(new ArrayList<>());
    }

    /**
     * Save a trader <b>Note</b>: This function creates a section for it's merchant
     *
     * @param conf Where to save the trader
     */
    public void saveMerchant(final ConfigurationSection conf) {
        final ConfigurationSection mainSection = TraderConfigManager.getSectionOrCreate(conf, this.internalName);
        if (mainSection.contains("Items")) {
            mainSection.set("Items", null);
        }
        final ConfigurationSection tradesSection = TraderConfigManager.getSectionOrCreate(mainSection, "Items");

        mainSection.set("DisplayName", this.displayName);

        for (int i = 0; i < this.trades.size(); i++) {
            tradesSection.set("Item " + i, this.trades.get(i));
        }
    }

    /**
     * @return the name
     */
    public String getInternalName() {
        return this.internalName;
    }

    /**
     * @return the trades
     */
    @SuppressWarnings("WeakerAccess")
    public List<ItemStack> getTrades() {
        return this.trades;
    }

    /**
     * @param trades the trades to set
     */
    public void setTrades(final List<ItemStack> trades) {
        this.trades = trades;
    }

    /**
     * @return the displayName
     */
    @SuppressWarnings("WeakerAccess")
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(final String displayName) {
        checkNotNull(displayName);
        this.displayName = displayName;
    }
}
