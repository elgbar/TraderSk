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

import ch.njol.skript.Skript;
import com.kh498.main.util.Util;
import me.cybermaxke.merchants.SMerchantPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * @author kh498
 */
public class Main extends JavaPlugin {
    static String CHAT_PREFIX;
    private static Plugin instance;
    private static boolean DEBUG;
    private final String MCVersion = Util.getNmsVersion(); //Minecraft server version
    private boolean enabled; //Used by onDisable to not save the traders if the plugin never fully enables
    static Plugin getInstance() {
        return instance;
    }

    public static void log(final String msg) {
        if (DEBUG) {
            getInstance().getLogger().log(Level.INFO, msg);
        }
    }

    @Override
    public void onDisable() {
        if (this.enabled) { TraderConfigManager.saveTraders(true); }

    }
    @Override
    public void onEnable() {
        CHAT_PREFIX = "[" + this.getName() + "] ";

        if (Bukkit.getPluginManager().getPlugin("Skript") == null) {
            this.getServer().getConsoleSender().sendMessage(CHAT_PREFIX + ChatColor.RED +
                                                            "Could not enable the plugin! This is an addon to Skript by Njol and cannot function without it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;

        TraderConfigManager.init(this);
        MainConfigManager.init(this);

        DEBUG = MainConfigManager.getMainConfig().getBoolean(MainConfigManager.DEBUG_PATH);

        final boolean versionMatch;

        boolean register = false;
        boolean oldVersion = false;

		/*
         * returns the merchant plugin or null if it is not found
		 */
        final Plugin merchants = Bukkit.getPluginManager().getPlugin("Merchants");

		/*
         * if you're using an older version of the API and the server runs on an internally supported version then use the internal version.
		 */

        final String internalAPIVersion = "1.5.1";
        if (merchants != null) {
            final String externalAPIVersion = merchants.getDescription().getVersion();

            if (externalAPIVersion.equals(internalAPIVersion)) {
                getLogger().info("Found plugins Merchants using the same version the API (" + externalAPIVersion +
                                 "). Using that instead of the internal version.");
            }
            // all old versions found on GitHub.com/Cybermaxke/MerchantsAPI (from 1.3.0-SNAPSHOT you can find the version in /gradle/build-base.gradle)
            else if (isUsingOldAPI(externalAPIVersion)) {
                //Only use internal version if the server runs a supported version
                if (supportedMCVersions()) {
                    //disable Merchants plugin
                    final Plugin plugin = Bukkit.getPluginManager().getPlugin("Merchants");
                    getServer().getPluginManager().disablePlugin(plugin);
                    oldVersion = true; //let the rest of the program that the external version was old

                    getLogger().info(
                        "Found plugins Merchants but its using an old version of the API (" + externalAPIVersion +
                        "). The plugin will be disabled and an internal, newer version (" + internalAPIVersion +
                        "), will be used in it's place.");
                }
                else {
                    getLogger().info(
                        "Found plugins Merchants but its using an old version or the same version of the API (" +
                        externalAPIVersion + "). Consider updating it.");
                }
            }
        }

        if (merchants == null || oldVersion) {
            final SMerchantPlugin SMerchantPlugin = new SMerchantPlugin(this);
            versionMatch = SMerchantPlugin.Enable();
            if (merchants == null && versionMatch) {
                getLogger().info("Could not find plugin Merchants, using internal API version " + internalAPIVersion +
                                 " with minecraft server version " + this.MCVersion);
            }
        }
        else {
            versionMatch = true;
            getLogger()
                .info("Using external Merchants plugin with the version " + merchants.getDescription().getVersion());
        }

        if (versionMatch) {
            Skript.registerAddon(this);
            if (Skript.isAcceptRegistrations()) {
                Register.RegisterMerchants(); // enable Merchants effect
                try {
                    TraderConfigManager.loadTraders();
                } catch (final NullPointerException e) {
                    getLogger().warning(
                        "Could not load traders due to you possibly using an old version of the config file! to fix this simply delete the config.yml in /plugins/tradersk/");
                    e.printStackTrace(); //get their attention

                }
                register = true;
            }
            else {
                Skript.error("Could not register effects as skript is not accepting registrations.");
            }
        }
        if (!versionMatch || !register) {
            this.getServer().getConsoleSender().sendMessage(CHAT_PREFIX + ChatColor.RED +
                                                            "Could not enable the plugin! Due to no internal support for minecraft version " +
                                                            this.MCVersion +
                                                            "! Please use the plugin Merchants by cybermaxke if you want to use this addon.");
            getServer().getPluginManager().disablePlugin(this);
        }
        else {
            getLogger().info("Merchants effects added!");
            getLogger().info("Enabled ~ Created by kh498");
            this.enabled = true;
        }

    }

    private boolean isUsingOldAPI(final String externalVer) {
        switch (externalVer) {
            case "1.5.0-SNAPSHOT":
            case "1.4.0-SNAPSHOT":
            case "1.3.0-SNAPSHOT":
            case "1.2.0-SNAPSHOT":
            case "1.2-SNAPSHOT":
            case "1.1.1-SNAPSHOT":
            case "1.1.0-SNAPSHOT":
            case "1.0.1-SNAPSHOT":
            case "1.0.0-SNAPSHOT":
                return true;
            default:
                return false;
        }
    }

    private boolean supportedMCVersions() {
        switch (this.MCVersion) {
            case "v111r1":
            case "v110r1":
            case "v19r2":
            case "v18r3":
                return true;
            default:
                return false;
        }
    }

}
