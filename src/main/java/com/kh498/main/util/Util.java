package com.kh498.main.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.kh498.main.MainConfigManager;

import ch.njol.skript.Skript;

public class Util
{
	/**
	 * @return The current Net Minecraft Server version, or <tt>unknown</tt> if it cannot find the version (this happens previous to 1.4)
	 */
	public static String getNmsVersion ()
	{
		String version = Bukkit.getServer ().getClass ().getPackage ().getName ().replace ("org.bukkit.craftbukkit", "").replaceFirst (".", "");
		version = version.replace ("_", "").toLowerCase ();
		if (version.isEmpty ())
		{
			version = "unknown";
		}
		return version;
	}

	/**
	 * Check if a material can be used in a merchant inventory, if not it would crash the client.
	 * 
	 * @param item
	 *            itemstack that will be checked
	 * @param canBeNull
	 *            Wherever the itemstack can be null
	 * @return True if it is valid, false if not
	 */
	public static boolean isValidMaterial (ItemStack item, boolean canBeNull)
	{
		if (item == null)
		{
			if (MainConfigManager.getMainConfig ().getBoolean (MainConfigManager.DEBUG_PATH))
				Skript.error ("Material was null when validating it");
			return canBeNull;
		}
		// below is list of illegal materials
		Material itemType = item.getType ();
		switch (itemType)
		{
			case DOUBLE_STONE_SLAB2:
			case NETHER_WARTS:
			case REDSTONE_COMPARATOR_ON:
			case REDSTONE_COMPARATOR_OFF:
			case FLOWER_POT:
			case TRIPWIRE:
			case REDSTONE_LAMP_ON:
			case POTATO:
			case CARROT:
			case CAULDRON:
			case BREWING_STAND:
			case SKULL:
			case WOODEN_DOOR:
			case SPRUCE_DOOR:
			case JUNGLE_DOOR:
			case DARK_OAK_DOOR:
			case ACACIA_DOOR:
			case BIRCH_DOOR:
			case DIODE_BLOCK_ON:
			case DIODE_BLOCK_OFF:
			case CAKE_BLOCK:
			case SUGAR_CANE_BLOCK:
			case IRON_DOOR_BLOCK:
			case SIGN_POST:
			case REDSTONE_WIRE:
			case WALL_SIGN:
			case COCOA:
			case AIR:
			case WATER:
			case LAVA:
			case STATIONARY_LAVA:
			case PORTAL:
			case ENDER_PORTAL:
			case PISTON_EXTENSION:
			case PISTON_MOVING_PIECE:
			case BED_BLOCK:
			case MELON_STEM:
			case FIRE:
				Skript.error ("Illegal material for one of the items '" + item.getType () + "'");
				return false;
			default:
				/* falls through */

		}
		return true;
	}

	/**
	 * Get the correct number of pages for a trader, this is needed because in one page there are three items.
	 * 
	 * @param list
	 *            List of traders
	 * @return Number of pages
	 */
	public static int getPages (List<ItemStack> list)
	{
		return Math.floorDiv (list.size () + 1, 3); // + 1 since the list starts
													// at zero
	}

	/**
	 * @author werter318
	 * 
	 * @param text
	 * @return String Json string
	 */
	public static String getJSON (String title)
	{
		char colorChar = ChatColor.COLOR_CHAR;

		String template = "{text:\"TEXT\",color:COLOR,bold:BOLD,underlined:UNDERLINED,italic:ITALIC,strikethrough:STRIKETHROUGH,obfuscated:OBFUSCATED,extra:[EXTRA]}";
		String json = "";

		List<String> parts = new ArrayList<String> ();

		int first = 0;
		int last = 0;

		while ((first = title.indexOf (colorChar, last)) != -1)
		{
			int offset = 2;
			while ((last = title.indexOf (colorChar, first + offset)) - 2 == first)
			{
				offset += 2;
			}

			if (last == -1)
			{
				parts.add (title.substring (first));
				break;
			} else
			{
				parts.add (title.substring (first, last));
			}
		}

		if (parts.isEmpty ())
		{
			parts.add (title);
		}

		Pattern colorFinder = Pattern.compile ("(" + colorChar + "([a-f0-9]))");
		for (String part : parts)
		{
			json = (json.isEmpty () ? template : json.replace ("EXTRA", template));

			Matcher matcher = colorFinder.matcher (part);
			ChatColor color = (matcher.find () ? ChatColor.getByChar (matcher.group ().charAt (1)) : ChatColor.WHITE);

			json = json.replace ("COLOR", color.name ().toLowerCase ());
			json = json.replace ("BOLD", String.valueOf (part.contains (ChatColor.BOLD.toString ())));
			json = json.replace ("ITALIC", String.valueOf (part.contains (ChatColor.ITALIC.toString ())));
			json = json.replace ("UNDERLINED", String.valueOf (part.contains (ChatColor.UNDERLINE.toString ())));
			json = json.replace ("STRIKETHROUGH", String.valueOf (part.contains (ChatColor.STRIKETHROUGH.toString ())));
			json = json.replace ("OBFUSCATED", String.valueOf (part.contains (ChatColor.MAGIC.toString ())));

			json = json.replace ("TEXT", part.replaceAll ("(" + colorChar + "([a-z0-9]))", ""));
		}

		json = json.replace (",extra:[EXTRA]", "");

		return json;
	}
}
