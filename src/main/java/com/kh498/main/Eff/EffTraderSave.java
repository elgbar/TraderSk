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

package com.kh498.main.Eff;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.kh498.main.TraderConfigManager;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffTraderSave extends Effect {

    @Override
    public boolean init(Expression<?>[] expr, int arg1, Kleenean arg2, ParseResult arg3) {
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean bool) {
        return "Saving all traders";
    }

    @Override
    protected void execute(Event e) {
        TraderConfigManager.saveTraders(false);
    }

}
