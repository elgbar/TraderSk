package com.kh498.main;

import java.util.HashMap;
import java.util.Map;

import me.cybermaxke.merchants.api.Merchants;
import me.cybermaxke.merchants.v18r3.SMerchantAPI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;

import com.kh498.main.trader.Trader;


/**
 * 
 * @author Elg
 *
 */
public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		@SuppressWarnings("unused")
		Trader Trader = new Trader(this);
		
		if (Bukkit.getPluginManager().getPlugin("Merchants") == null) {
			//SMerchantPlugin.onEnable();
			Merchants.set(new SMerchantAPI());
			getLogger().info("Could not find Merchants. Using the default version v1_8_R3");
		} else {
			getLogger().info("Found Merchants using the it's version of the API");
		}
		
		Skript.registerAddon(this);
		boolean merchant = false;
		if (Skript.isAcceptRegistrations()) {
			merchant = Register.RegisterMerchants(); //enable Merchants effects
			loadTraders();
		} else {
			Skript.error("Could not register effects as skript is not accepting registrations.");
		}
		if (merchant) {
			getLogger().info("Merchants effects added!");
			getLogger().info("Enabled ~ Created by kh498");
		} else {
			getLogger().info("Could not enable the plugin!");
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable() {
		saveTraders(true);
	}
	
	public void saveTraders(boolean log){
		Map<String, Object> Traders = Trader.getTrader();
		if (Traders.size() != 0){
			this.getConfig().createSection("Traders", Traders);
			if (log)
				getLogger().info("Traders saved.");		
		} else {
			if (log)
				getLogger().info("Could not save config as there are no traders.");
		}
		this.saveConfig();
	}
	
	public void loadTraders(){
		getConfig();
		try {
			Map<String, Object> map = this.getConfig().getConfigurationSection("Traders").getValues(false);
			Trader.setTrader(map);
			getLogger().info("Traders loaded!");
		} catch (NullPointerException e) {
			getLogger().info("No traders loaded.");
		}
	}

	public Map<String, String> convert(Map<String, Object> map){
		Map<String,String> newMap = new HashMap<String,String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
		       if(entry.getValue() instanceof String){
		            newMap.put(entry.getKey(), (String) entry.getValue());
		          }
		 }
		return newMap;
	}
}
