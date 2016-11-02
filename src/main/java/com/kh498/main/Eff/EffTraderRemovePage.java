/**
 *  This file is part of TraderSk
 *  
 *  Copyright (C) 2016, kh498
 * 
 *  TraderSk is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TraderSk is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TraderSk.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kh498.main.Eff;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import com.kh498.main.trader.TradeMerchant;
import com.kh498.main.trader.Trader;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffTraderRemovePage extends Effect
{
	private Expression<Number> page;
	private Expression<String> trader;

	@ Override
	@ SuppressWarnings ("unchecked")
	public boolean init (Expression<?>[] expr, int arg1, Kleenean arg2, ParseResult arg3)
	{

		this.page = (Expression<Number>) expr[0];
		this.trader = (Expression<String>) expr[1];
		return true;
	}

	@ Override
	public String toString (@ Nullable Event arg0, boolean arg1)
	{
		return "Remove page from Trader";
	}

	@ Override
	protected void execute (Event e)
	{
		Integer page;
		TradeMerchant trader;
		try
		{
			page = this.page.getSingle (e).intValue ();
			trader = Trader.getTradeMerchant (this.trader.getSingle (e));
		} catch (Exception ex)
		{
			return;
		}

		if (page % 1 != 0)
		{
			return;
		}

		if (trader == null || page == null)
		{
			return;
		}
		Trader.TraderRemovePage (trader, page);

	}

}
