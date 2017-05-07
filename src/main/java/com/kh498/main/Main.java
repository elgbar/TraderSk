/**
 * This file is part of TraderSk
 * <p>
 * Copyright (C) 2016, kh498
 * <p>
 * TraderSk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * TraderSk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
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
    private final String MCversion = Util.getNmsVersion(); //Minecraft server version
    private boolean enabled; //Used by onDisable to not save the traders if the plugin never fully enables
    public static Plugin getInstance() {
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
         * returns true if you have the plugin Merchants and false if not
		 */
        final boolean hasMerchants = Bukkit.getPluginManager().getPlugin("Merchants") != null;

		/*
         * if you're using an older version of the API and the server runs on an internally supported version then use the internal version.
		 */
        final String APIversion = "1.5.0-SNAPSHOT";
        if (hasMerchants) {
            final String externalVer = Bukkit.getPluginManager().getPlugin("Merchants").getDescription().getVersion();

            // all old versions found on GitHub.com/Cybermaxke/MerchantsAPI (from 1.3.0-SNAPSHOT you can find the version in /gradle/build-base.gradle)
            if ("1.4.0-SNAPSHOT".equals(externalVer) || "1.3.0-SNAPSHOT".equals(externalVer) ||
                "1.2.0-SNAPSHOT".equals(externalVer) || "1.2-SNAPSHOT".equals(externalVer) ||
                "1.1.1-SNAPSHOT".equals(externalVer) || "1.1.0-SNAPSHOT".equals(externalVer) ||
                "1.0.1-SNAPSHOT".equals(externalVer) || "1.0.0-SNAPSHOT".equals(externalVer)) {
                //Only use internal version if the server runs a supported version
                if ("v18r3".equals(this.MCversion) || "v19r2".equals(this.MCversion) ||
                    "v110r1".equals(this.MCversion) ||
                    "v111r1".equals(this.MCversion)) { //TODO add universal support, so it's easier to maintain
                    //disable Merchants plugin
                    final Plugin plugin = Bukkit.getPluginManager().getPlugin("Merchants");
                    getServer().getPluginManager().disablePlugin(plugin);
                    oldVersion = true; //let the rest of the program that the external version was old

                    getLogger().info("Found plugins Merchants but its using an old version of the API (" + externalVer +
                                     "). The plugin will be disabled and an internal, newer version (" + APIversion +
                                     "), will be used in it's place.");
                }
                else {
                    getLogger().info(
                        "Found plugins Merchants but its using an old version or the same version of the API (" +
                        externalVer + "). Consider updating it.");

                }
            }
        }

        if (!hasMerchants || oldVersion) {
            final SMerchantPlugin SMerchantPlugin = new SMerchantPlugin(this);
            versionMatch = SMerchantPlugin.Enable();
            if (!hasMerchants && versionMatch) {
                getLogger().info("Could not find plugin Merchants, using internal API version " + APIversion +
                                 " with minecraft server version " + this.MCversion);
            }
        }
        else {
            versionMatch = true;
            getLogger().info("Found Merchants, using it's version");
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
                                                            this.MCversion +
                                                            "! Please use the plugin Merchants by cybermaxke if you want to use this addon.");
            getLogger().log(Level.ALL, "Register: " + register + " | versionMatch: " + versionMatch);
            getServer().getPluginManager().disablePlugin(this);
        }
        else {
            getLogger().info("Merchants effects added!");
            getLogger().info("Enabled ~ Created by kh498");
            this.enabled = true;
        }

    }

}
