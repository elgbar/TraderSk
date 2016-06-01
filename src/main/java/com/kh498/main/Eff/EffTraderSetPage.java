package com.kh498.main.Eff;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import com.kh498.main.trader.Trader;

public class EffTraderSetPage extends Effect {
	private Expression<Number> page;
	private Expression<String> trader;
	private Expression<ItemStack> item0;
	private Expression<ItemStack> item1;
	private Expression<ItemStack> item2;

	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expr, int arg1, Kleenean arg2,
			ParseResult arg3) {
		page = (Expression<Number>) expr[0];
		trader = (Expression<String>) expr[1];
		item0 = (Expression<ItemStack>) expr[2];
		item1 = (Expression<ItemStack>) expr[3];
		item2 = (Expression<ItemStack>) expr[4];
		return true;
	}

	public String toString(@Nullable Event arg0, boolean arg1) {
		return "Set page in trader";
	}

	@Override
	protected void execute(Event e) {
		Integer page  = ((Number) this.page.getSingle(e)).intValue();
		
		if (page % 1 != 0){
			return;
		}
		
		String trader = this.trader.getSingle(e);
		ItemStack item0 = this.item0.getSingle(e);
		ItemStack item1 = this.item1.getSingle(e);
		ItemStack item2;
		
		// page2 can be null
		try {
			item2 = this.item2.getSingle(e);
		} catch (NullPointerException ex) {
			item2 = null;
		}

		if (page == null || trader == null || item0 == null || item1 == null) {
			return;
		}
		Trader.TraderSetPage(trader, page, item0, item1, item2);
	}

}
