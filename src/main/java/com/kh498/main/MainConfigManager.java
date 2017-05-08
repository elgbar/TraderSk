package com.kh498.main;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;

public class MainConfigManager {
    public static final String DEBUG_PATH = "debug";
    public static final String OPEN_EMPTY_PATH = "openEmptyTraders";
    private static final String CONF_NAME = "config.yml";
    private static YamlConfiguration myConfig;
    /**
     * Load config.yml or create it isn't found
     *
     * @param p Plugin to load
     */
    public static void init(final Plugin p) {
        final File configFile = new File(p.getDataFolder(), CONF_NAME);
        if (configFile.exists()) {
            myConfig = new YamlConfiguration();
            try {
                p.getLogger().info("Main config loaded");
                myConfig.load(configFile);
            } catch (final Exception ex) {
                p.getLogger().warning("Failed to load config.yml (try to delete it)");
                ex.printStackTrace();
            }

        }
        else {
            try {
                if (!p.getDataFolder().mkdir()) {
                    Main.getInstance().getLogger().log(Level.SEVERE, "Failed to create data folder!");
                }
                final InputStream jarURL = MainConfigManager.class.getResourceAsStream("/" + CONF_NAME);
                copyFile(jarURL, configFile);
                myConfig = new YamlConfiguration();
                myConfig.load(configFile);
                p.getLogger().info("Main config created");
            } catch (final Exception ex) {
                p.getLogger().warning("Failed to create config.yml from the jar");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Copies a file to a new location.
     *
     * @param in  InputStream
     * @param out File
     *
     * @throws Exception If the copy fails
     */
    private static void copyFile(final InputStream in, final File out) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(out)) {
            final byte[] buf = new byte[1024];
            int i;
            while ((i = in.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } finally {
            if (in != null) {
                in.close();
            }

        }
    }

    /**
     * @return the main config
     */
    public static YamlConfiguration getMainConfig() {
        return myConfig;
    }
}