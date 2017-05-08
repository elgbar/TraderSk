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

package com.kh498.main;

import com.kh498.main.trader.TradeMerchant;
import com.kh498.main.trader.Trader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TraderConfigManager {
    private final static String TRADERS_CONF_SEC = "Traders";

    private static YamlConfiguration traderConfig = null;
    private static File traderConfigFile = null;
    private static Plugin plugin = null;

    public static void init(final Plugin p) {
        plugin = p;
        traderConfigFile = new File(p.getDataFolder().getAbsolutePath());
        if (!traderConfigFile.exists() || !traderConfigFile.isDirectory()) {
            final boolean mkDir = traderConfigFile.mkdir();
            if (!mkDir) {
                Main.getInstance().getLogger().log(Level.SEVERE, "Failed to create data folder directory");
            }
        }
        traderConfigFile = new File(p.getDataFolder().getAbsolutePath(), "Traders.yml");
        try {
            if (!traderConfigFile.exists()) {
                final boolean newFile = traderConfigFile.createNewFile();
                if (!newFile) {
                    Main.getInstance().getLogger().log(Level.SEVERE, "Failed to create trader trader config file");
                }
            }
            traderConfig = YamlConfiguration.loadConfiguration(traderConfigFile);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save traders from memory to disk
     *
     * @param log Boolean to determinate wherever to log info or not
     */
    public static void saveTraders(final boolean log) {
        final Map<String, TradeMerchant> traders = Trader.getTraders();
        if (traders.size() != 0) {
            try {
                final ConfigurationSection configSec = getSectionOrCreate(traderConfig, TRADERS_CONF_SEC);
                for (final TradeMerchant tm : traders.values()) {
                    tm.saveMerchant(configSec);
                }

                if (log) { plugin.getLogger().info("Traders saved"); }

            } catch (final Exception e) {
                e.printStackTrace();
                plugin.getLogger().warning("Could not create sections for traders");
            }
        }
        else {
            if (traderConfig.isSet(TRADERS_CONF_SEC)) {
                traderConfig.set(TRADERS_CONF_SEC, null);
            }
        }
        try {
            saveTraderConfig();
        } catch (final NullPointerException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not save traders");
        }
    }

    public static void removeTrader(final TradeMerchant tm) {
        final ConfigurationSection configSec = traderConfig.getConfigurationSection(TRADERS_CONF_SEC);
        if (configSec.contains(tm.getInternalName())) { configSec.set(tm.getInternalName(), null); }
    }

    public static ConfigurationSection getSectionOrCreate(final ConfigurationSection parent, final String sectionName) {
        ConfigurationSection section = parent.getConfigurationSection(sectionName);
        if (section == null) { section = parent.createSection(sectionName); }
        return section;
    }

    /**
     * Load the traders from disk to memory
     */
    static void loadTraders() {
        final ConfigurationSection mainSec = TraderConfigManager.getConfig().getConfigurationSection(TRADERS_CONF_SEC);
        final Map<String, Object> map;
        try {
            map = mainSec.getValues(false);
        } catch (final NullPointerException e) {
            Main.getInstance().getServer().getConsoleSender()
                .sendMessage(Main.CHAT_PREFIX + "Could not find any traders to load");
            return;
        }

        final Map<String, TradeMerchant> newMap = new HashMap<>();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            final TradeMerchant newTM = new TradeMerchant(entry.getKey());
            final ConfigurationSection traderSec = mainSec.getConfigurationSection(entry.getKey());

            if (traderSec.getString("DisplayName") != null) {
                newTM.setDisplayName(traderSec.getString("DisplayName"));
            }
            final ConfigurationSection itemSec = traderSec.getConfigurationSection("Items");
            if (itemSec != null) {
                final ArrayList<ItemStack> list = new ArrayList<>();
                Boolean foundNext = true;
                int nr = 0;
                while (foundNext) {
                    if (itemSec.isItemStack("Item " + nr)) {
                        list.add(itemSec.getItemStack("Item " + nr));
                        nr++;
                    }
                    else { foundNext = false; }
                }
                newTM.setTrades(list);
            }
            newMap.put(entry.getKey(), newTM);
        }

        Trader.setTraders(newMap);
        Main.getInstance().getServer().getConsoleSender().sendMessage(Main.CHAT_PREFIX + "Traders loaded");
    }

    private static YamlConfiguration getConfig() {
        return traderConfig;
    }

    private static void saveTraderConfig() {
        if (traderConfig != null) {
            try {
                traderConfig.save(traderConfigFile);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

}
