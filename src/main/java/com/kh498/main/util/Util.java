package com.kh498.main.util;

import org.bukkit.Bukkit;

public class Util {
	/**
	 * 
	 * @return The current Net Minecraft Server version, or <tt>unknown</tt> if it cannot find the version (this happens previous to 1.4)
	 */
	public static String getNmsVersion() {
		String version =  Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "").replaceFirst(".", "");
		version = version.replace("_", "").toLowerCase();
		if (version.isEmpty()) {
			version = "unknown";
		}
		return version;
	}
}
