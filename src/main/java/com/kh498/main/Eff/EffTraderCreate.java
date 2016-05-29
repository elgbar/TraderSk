package com.kh498.main.Eff;

import org.bukkit.event.Event;

import com.kh498.main.trader.Trader;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import javax.annotation.Nullable;

public class EffTraderCreate extends Effect {
	private Expression<String> trader;

	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expr, int arg1, Kleenean arg2,
			ParseResult arg3) {
		this.trader = (Expression<String>) expr[0];
		return true;
	}

	public String toString(@Nullable Event e, boolean bool) {
		return "Create a trader";
	}

	@Override
	protected void execute(Event e) {
		String trader = this.trader.getSingle(e);
		if (trader == null) {
			return;
		}
		Trader.TraderNew(trader);
	}

}
