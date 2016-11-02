package com.kh498.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MainConfigManager
{
	private static Plugin plugin = null;
	private static YamlConfiguration myConfig;
	private static File configFile;

	private static final String CONF_NAME = "config.yml";

	public static final String DEBUG_PATH = "debug";
	public static final String OPEN_EMPTY_PATH = "openEmptyTraders";

	/**
	 * Load config.yml or create it isn't found
	 * 
	 * @param p
	 *            Plugin to load
	 */
	public static void init (Plugin p)
	{
		plugin = p;

		configFile = new File (p.getDataFolder (), CONF_NAME);
		if (configFile.exists ())
		{
			myConfig = new YamlConfiguration ();
			try
			{
				plugin.getLogger ().info ("Main config loaded");
				myConfig.load (configFile);
			} catch (Exception ex)
			{
				plugin.getLogger ().warning ("Failed to load config.yml (try to delete it)");
				ex.printStackTrace ();
			}

		} else
		{
			try
			{
				plugin.getDataFolder ().mkdir ();
				InputStream jarURL = MainConfigManager.class.getResourceAsStream ("/" + CONF_NAME);
				copyFile (jarURL, configFile);
				myConfig = new YamlConfiguration ();
				myConfig.load (configFile);
				plugin.getLogger ().info ("Main config created");
			} catch (Exception ex)
			{
				plugin.getLogger ().warning ("Failed to create config.yml from the jar");
				ex.printStackTrace ();
			}
		}
	}

	/**
	 * Copies a file to a new location.
	 * 
	 * @param in
	 *            InputStream
	 * @param out
	 *            File
	 * @throws Exception
	 */
	private static void copyFile (InputStream in, File out) throws Exception
	{
		InputStream fis = in;
		FileOutputStream fos = new FileOutputStream (out);
		try
		{
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read (buf)) != -1)
			{
				fos.write (buf, 0, i);
			}
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			if (fis != null)
			{
				fis.close ();
			}
			if (fos != null)
			{
				fos.close ();
			}
		}
	}

	/**
	 * @return the main config
	 */
	public static YamlConfiguration getMainConfig ()
	{
		return myConfig;
	}
}