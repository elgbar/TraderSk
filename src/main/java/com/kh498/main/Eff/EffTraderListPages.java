/*
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
import com.kh498.main.trader.TradeMerchant;
import com.kh498.main.trader.Trader;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffTraderListPages extends Effect {
    private Expression<Player> player;
    private Expression<String> trader;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(final Expression<?>[] expr, final int arg1, final Kleenean arg2, final ParseResult arg3) {
        this.trader = (Expression<String>) expr[0];
        this.player = (Expression<Player>) expr[1];
        return true;
    }

    @Override
    public String toString(@Nullable final Event e, final boolean bool) {
        return "List all items for a trader";
    }

    @Override
    protected void execute(final Event e) {
        final Player player;
        final TradeMerchant trader;
        try {
            player = this.player.getSingle(e);
            trader = Trader.getTradeMerchant(this.trader.getSingle(e));
        } catch (final Exception ex) {
            return;
        }
        if (player == null) {
            return;
        }
        else if (trader == null) {
            player.sendMessage("Could not find the trader. Here is a list of all traders:");
            for (final TradeMerchant t : Trader.getTraders().values()) {
                player.sendMessage(t.getInternalName());
            }
            return;
        }
        Trader.TraderListPages(trader, player);
    }

}
