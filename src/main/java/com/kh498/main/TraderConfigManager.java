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

public class TraderConfigManager {
    private final static String TRADERS_CONF_SEC = "Traders";

    private static YamlConfiguration traderConfig = null;
    private static File traderConfigFile = null;
    private static Plugin plugin = null;

    public static void init(final Plugin p) {
        plugin = p;
        traderConfigFile = new File(p.getDataFolder().getAbsolutePath());
        if (!traderConfigFile.exists() || !traderConfigFile.isDirectory()) { traderConfigFile.mkdir(); }
        traderConfigFile = new File(p.getDataFolder().getAbsolutePath(), "Traders.yml");
        try {
            if (!traderConfigFile.exists()) { traderConfigFile.createNewFile(); }
            traderConfig = YamlConfiguration.loadConfiguration(traderConfigFile);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Save traders from memory to disk
     *
     * @param log Boolean to determinte wherever to log info or not
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
            final TradeMerchant newTM = new TradeMerchant(entry.getKey(), null);
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
