/**
 * This file is part of TraderSk, modified file from MerchantsAPI
 * 
 * Copyright (c) 2014, Cybermaxke
 * Copyright (C) 2016, kh498
 * 
 * TraderSk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TraderSk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with TraderSk. If not, see <http://www.gnu.org/licenses/>.
 */

package me.cybermaxke.merchants;

import java.util.logging.Level;

import com.kh498.main.Main;
import com.kh498.main.util.Util;

import me.cybermaxke.merchants.api.MerchantAPI;
import me.cybermaxke.merchants.api.Merchants;

public class SMerchantPlugin {

	private static Main instance;

	public SMerchantPlugin(Main instance) {
		SMerchantPlugin.instance = instance;
	}
	
	public boolean Enable() {
		String version = Util.getNmsVersion();
		String clazzName = this.getClass().getPackage().getName() + "." + version + ".SMerchantAPI";

		try {
			Class<?> clazz = Class.forName(clazzName);

			if (MerchantAPI.class.isAssignableFrom(clazz)) {
				try {
					MerchantAPI api = (MerchantAPI) clazz.newInstance();
					Merchants.set(api);
					return true;
				} catch (Exception e) {
				}
			}

			instance.getLogger().log(Level.WARNING, "Plugin could not be loaded, version " + version + " it's implementation is invalid!");
			return false;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
