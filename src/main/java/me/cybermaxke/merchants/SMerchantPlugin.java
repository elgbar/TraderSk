/**
 * This file is part of TraderSk, modified file from MerchantsAPI
 * <p>
 * Copyright (c) 2014, Cybermaxke
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
 * along with TraderSk. If not, see <http://www.gnu.org/licenses/>.
 */

package me.cybermaxke.merchants;

import com.kh498.main.Main;
import com.kh498.main.util.Util;
import me.cybermaxke.merchants.api.MerchantAPI;
import me.cybermaxke.merchants.api.Merchants;

import java.util.logging.Level;

public class SMerchantPlugin {

    private static Main instance;

    public SMerchantPlugin(final Main instance) {
        SMerchantPlugin.instance = instance;
    }

    public boolean Enable() {
        final String version = Util.getNmsVersion();
        final String clazzName = this.getClass().getPackage().getName() + "." + version + ".SMerchantAPI";

        try {
            final Class<?> clazz = Class.forName(clazzName);

            if (MerchantAPI.class.isAssignableFrom(clazz)) {
                try {
                    final MerchantAPI api = (MerchantAPI) clazz.newInstance();
                    Merchants.set(api);
                    return true;
                } catch (final Exception e) {
                    //ignore
                }
            }

            instance.getLogger().log(Level.WARNING, "Plugin could not be loaded, version " + version +
                                                    " it's implementation is invalid!");
            return false;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

}
