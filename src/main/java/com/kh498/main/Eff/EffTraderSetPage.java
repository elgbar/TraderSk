/*
  This file is part of TraderSk
  <p>
  Copyright (C) 2016, kh498
  <p>
  TraderSk is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  TraderSk is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with TraderSk.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kh498.main.Eff;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.kh498.main.trader.TradeMerchant;
import com.kh498.main.trader.Trader;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class EffTraderSetPage extends Effect {
    private Expression<Number> page;
    private Expression<String> trader;
    private Expression<ItemStack> item0;
    private Expression<ItemStack> item1;
    private Expression<ItemStack> item2;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(final Expression<?>[] expr, final int arg1, final Kleenean arg2, final ParseResult arg3) {
        this.page = (Expression<Number>) expr[0];
        this.trader = (Expression<String>) expr[1];
        this.item0 = (Expression<ItemStack>) expr[2];
        this.item1 = (Expression<ItemStack>) expr[3];
        this.item2 = (Expression<ItemStack>) expr[4];
        return true;
    }

    @Override
    public String toString(@Nullable final Event arg0, final boolean arg1) {
        return "Set page in trader";
    }

    @Override
    protected void execute(final Event e) {
        final Integer page;
        try {
            page = this.page.getSingle(e).intValue();
        } catch (final NullPointerException ex) {
            return;
        }

        final TradeMerchant trader = Trader.getTradeMerchant(this.trader.getSingle(e));
        final ItemStack item0 = this.item0.getSingle(e);
        final ItemStack item1 = this.item1.getSingle(e);
        ItemStack item2;

        // page2 can be null
        try {
            item2 = this.item2.getSingle(e);
        } catch (final NullPointerException ex) {
            item2 = null;
        }

        if (trader == null || item0 == null || item1 == null) {
            return;
        }
        Trader.TraderSetPage(trader, page, item0, item1, item2);
    }

}
