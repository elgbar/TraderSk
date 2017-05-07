package com.kh498.main.util;

import ch.njol.skript.Skript;
import com.kh498.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Util {
    /**
     * @return The current Net Minecraft Server version, or <tt>unknown</tt> if it cannot find the version (this happens previous to 1.4)
     */
    public static String getNmsVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "")
                               .replaceFirst(".", "");
        version = version.replace("_", "").toLowerCase();
        if (version.isEmpty()) {
            version = "unknown";
        }
        return version;
    }

    /**
     * Check if a material can be used in a merchant inventory, if not it would crash the client.
     *
     * @param item      itemstack that will be checked
     * @param canBeNull Wherever the itemstack can be null
     *
     * @return True if it is valid, false if not
     */
    public static boolean isValidMaterial(final ItemStack item, final boolean canBeNull) {
        if (item == null) {
            Main.log("Material was null when validating it");
            return canBeNull;
        }
        // below is list of illegal materials
        final Material itemType = item.getType();
        switch (itemType) {
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
                Skript.error("Illegal material for one of the items '" + item.getType() + "'");
                return false;
            default:
                /* falls through */

        }
        return true;
    }

    /**
     * Get the correct number of pages for a trader, this is needed because in one page there are three items.
     *
     * @param list List of traders
     *
     * @return Number of pages
     */
    public static int getPages(final List<ItemStack> list) {
        return Math.floorDiv(list.size() + 1, 3); // + 1 since the list starts
        // at zero
    }

    /**
     * @param title The non-json title that will be converted into a JSON string
     *
     * @return String Json string
     */
    public static String toJSON(final String title) {
        return "{\"text\":\"" + title + "\"}";
    }
}
