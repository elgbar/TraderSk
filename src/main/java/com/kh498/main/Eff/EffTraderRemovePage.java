package com.kh498.main.Eff;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import com.kh498.main.trader.Trader;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffTraderRemovePage extends Effect {
	private Expression<Number> page;
	private Expression<String> trader;

	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expr, int arg1, Kleenean arg2,
			ParseResult arg3) {

		this.page = (Expression<Number>) expr[0];
		this.trader = (Expression<String>) expr[1];
		return true;
	}

	public String toString(@Nullable Event arg0, boolean arg1) {
		return "Remove page from Trader";
	}

	@Override
	protected void execute(Event e) {
		Integer page  = ((Number) this.page.getSingle(e)).intValue();
		String trader = this.trader.getSingle(e);
		
		if (page % 1 != 0){
			return;
		}
		
		if (trader == null || page == null) {
			return;
		}
		Trader.TraderRemovePage(trader, page);

	}

}
