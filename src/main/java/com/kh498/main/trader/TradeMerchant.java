package com.kh498.main.trader;

import com.kh498.main.TraderConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TradeMerchant {
    private final String internalName;
    private String displayName;
    private List<ItemStack> trades;

    public TradeMerchant(final String name, @Nullable final ArrayList<ItemStack> traders) {
        this.internalName = name;
        Trader.TraderSetTitle(this, name);
        if (traders != null) {
            this.setTrades(traders);
        }
        else {
            this.setTrades(new ArrayList<>());
        }
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
