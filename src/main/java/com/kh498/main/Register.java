package com.kh498.main;

import ch.njol.skript.Skript;

import com.kh498.main.Eff.EffTraderCreate;
import com.kh498.main.Eff.EffTraderOpen;
import com.kh498.main.Eff.EffTraderRemovePage;
import com.kh498.main.Eff.EffTraderSetPage;

/**
 * 
 * @author karl henrik
 *
 */
public class Register {

	public static boolean RegisterMerchants() {
		
		Skript.registerEffect(EffTraderCreate.class,
				"(create|make) [a] [merchant] trader [named] %string%");
		Skript.registerEffect(EffTraderCreate.class,
				"(remove|clear) [the] [merchant] trader [named] %string%");
		Skript.registerEffect(
				EffTraderSetPage.class,
				"set items in page %number% (for|of) [merchant] trader %string% to %itemstack% as output[ item](,| and) %itemstack%[ and %-itemstack%] as input[ item[s]]");
		Skript.registerEffect(EffTraderRemovePage.class,
				"(remove|clear) page %number% (from|of|for) [merchant] trader %string%");
		Skript.registerEffect(EffTraderOpen.class,
				"(open|show) [merchant] trader %string% to %player%");
		// Skript.registerEffect(EffTraderListPages.class,
		// "list all items (for|in) [merchant] trader %string% to %player%");
		return true;
	}
}
