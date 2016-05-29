package com.kh498.main.Eff;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.kh498.main.trader.Trader;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffTraderOpen extends Effect {
	private Expression<Player> player;
	private Expression<String> trader;

	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expr, int arg1, Kleenean arg2,
			ParseResult arg3) {
		trader = (Expression<String>) expr[0];
		player = (Expression<Player>) expr[1];
		return true;
	}

	public String toString(@Nullable Event e, boolean bool) {
		return "Open Trader GUI to player";
	}

	@Override
	protected void execute(Event e) {
		Player player = this.player.getSingle(e);
		String trader = this.trader.getSingle(e);
		if (trader == null || player == null) {
			return;
		}
		Trader.TraderOpen(trader, player);
	}

}
