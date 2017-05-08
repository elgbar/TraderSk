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
import com.kh498.main.Eff.*;

/**
 * @author kh498
 */
class Register {

    static void RegisterMerchants() {

        Skript.registerEffect(EffTraderCreate.class, "(create|make) [a] [merchant] trader [named] %string%");
        Skript.registerEffect(EffTraderRemove.class, "(remove|clear) [the] [merchant] trader [named] %string%");
        Skript.registerEffect(EffTraderSetPage.class,
                              "set items in page %number% (for|of) [merchant] trader %string% to %itemstack% as output[ item](,| and) %itemstack%[ and %-itemstack%] as input[ item[s]]");
        Skript.registerEffect(EffTraderRemovePage.class,
                              "(remove|clear|delete) page %number% (from|of|for) [merchant] trader %string%");
        Skript.registerEffect(EffTraderRemoveAll.class, "(remove|clear|delete) all [merchant] traders");
        Skript.registerEffect(EffTraderOpen.class, "(open|show) [merchant] trader %string% to %player%");
        Skript
            .registerEffect(EffTraderRename.class, "(rename|set name [of|for]) [merchant] trader %string% to %string%");
        Skript.registerEffect(EffTraderSave.class, "save all [merchant] trader[s]");

        if (MainConfigManager.getMainConfig().getBoolean(MainConfigManager.DEBUG_PATH)) {
            Main.log("Debug effect has been enabled");
            Skript.registerEffect(EffTraderListPages.class, "list all items for trader %string% to %player%");
        }
    }
}
