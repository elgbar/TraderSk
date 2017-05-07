/*
 * This file is part of MerchantsAPI.
 *
 * Copyright (c) Cybermaxke
 *
 * MerchantsAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MerchantsAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MerchantsAPI. If not, see <http://www.gnu.org/licenses/>.
 */
package me.cybermaxke.merchants.v110r1;

import me.cybermaxke.merchants.api.MerchantOffer;
import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.MerchantRecipe;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public class SMerchantOffer extends MerchantRecipe implements MerchantOffer {

    // The merchants this offer is added to
    private final Set<SMerchant> merchants = Collections.newSetFromMap(new WeakHashMap<SMerchant, Boolean>());

    private final org.bukkit.inventory.ItemStack item1;
    private final org.bukkit.inventory.ItemStack item2;
    private final org.bukkit.inventory.ItemStack result;

    private int maxUses = -1;
    private int uses;

    SMerchantOffer(final org.bukkit.inventory.ItemStack result, final org.bukkit.inventory.ItemStack item1,
                   final org.bukkit.inventory.ItemStack item2) {
        super(null, null, null);

        this.result = result;
        this.item1 = item1;
        this.item2 = item2;
    }
    @SuppressWarnings("deprecation")
    private static ItemStack convertSafely(final org.bukkit.inventory.ItemStack itemStack) {
        if (itemStack == null || itemStack.getTypeId() == 0 || itemStack.getAmount() == 0) {
            return null;
        }
        return CraftItemStack.asNMSCopy(itemStack);
    }
    // Links the offer to the merchant.
    void add(final SMerchant merchant) {
        this.merchants.add(merchant);
    }
    // Unlinks the offer from the merchant.
    void remove(final SMerchant merchant) {
        this.merchants.remove(merchant);
    }
    @Override
    public org.bukkit.inventory.ItemStack getFirstItem() {
        return this.item1.clone();
    }
    @Override
    public Optional<org.bukkit.inventory.ItemStack> getSecondItem() {
        if (this.item2 == null) {
            return Optional.empty();
        }

        return Optional.of(this.item2.clone());
    }
    @Override
    public org.bukkit.inventory.ItemStack getResultItem() {
        return this.result.clone();
    }
    @Override
    public int getMaxUses() {
        return this.maxUses;
    }
    @Override
    public void setMaxUses(final int uses) {
        if (this.maxUses == uses) {
            return;
        }

        // Get the state before
        final boolean locked0 = this.isLocked();
        // Set the max uses
        this.maxUses = uses;
        // Get the state after
        final boolean locked1 = this.isLocked();

        // Send the new offer list
        if (locked0 != locked1) {
            for (final SMerchant merchant : this.merchants) {
                merchant.sendUpdate();
            }
        }
    }
    @Override
    public void addMaxUses(final int extra) {
        if (this.maxUses >= 0 && extra != 0) {
            this.setMaxUses(this.maxUses + extra);
        }
    }
    @Override
    public int getUses() {
        return this.uses;
    }
    @Override
    public void setUses(final int uses) {
        if (this.uses == uses) {
            return;
        }

        // Get the state before
        final boolean locked0 = this.isLocked();
        // Add the uses
        this.uses = uses;
        // Get the state after
        final boolean locked1 = this.isLocked();

        // Send the new offer list
        if (locked0 != locked1) {
            for (final SMerchant merchant : this.merchants) {
                merchant.sendUpdate();
            }
        }
    }
    @Override
    public void addUses(final int uses) {
        if (uses != 0) {
            this.setUses(this.uses + uses);
        }
    }
    @Override
    public boolean isLocked() {
        return this.maxUses >= 0 && this.uses >= this.maxUses;
    }
    @Override
    public ItemStack getBuyItem1() {
        return convertSafely(this.item1);
    }
    @Override
    public ItemStack getBuyItem2() {
        return convertSafely(this.item2);
    }
    @Override
    public boolean hasSecondItem() {
        return this.item2 != null;
    }
    @Override
    public ItemStack getBuyItem3() {
        return convertSafely(this.result);
    }
    @Override
    public int e() {
        return this.uses;
    }
    @Override
    public int f() {
        return this.maxUses < 0 ? Integer.MAX_VALUE : this.maxUses;
    }
    @Override
    public void g() {
        this.addUses(1);
    }
    @Override
    public void a(final int extra) {
        this.addMaxUses(extra);
    }
    @Override
    public boolean h() {
        return this.isLocked();
    }
    @Override
    public boolean j() {
        return false;
    }
    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public SMerchantOffer clone() {
        final org.bukkit.inventory.ItemStack result = this.result.clone();
        final org.bukkit.inventory.ItemStack item1 = this.item1.clone();
        final org.bukkit.inventory.ItemStack item2 = this.item2 != null ? this.item2.clone() : null;

        final SMerchantOffer clone = new SMerchantOffer(result, item1, item2);
        clone.maxUses = this.maxUses;
        clone.uses = this.uses;

        return clone;
    }
}
