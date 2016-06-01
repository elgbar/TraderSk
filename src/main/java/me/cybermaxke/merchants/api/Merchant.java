/**
 * This file is part of MerchantsAPI.
 * 
 * Copyright (c) 2014, Cybermaxke
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
package me.cybermaxke.merchants.api;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.bukkit.entity.Player;

public interface Merchant {

	/**
	 * Gets the title of the merchant.
	 * 
	 * @return the title
	 */
	String getTitle();
	
	/**
	 * Gets whether the string in the json format is.
	 * 
	 * @return is title json
	 */
	boolean isTitleJson();
	
	/**
	 * Sets the title of the merchant.
	 * 
	 * @param title the title
	 * @param jsonTitle whether the title in json format is
	 */
	void setTitle(String title, boolean jsonTitle);
	
	/**
	 * Sets the title of the merchant.
	 * 
	 * @param title the title
	 */
	void setTitle(String title);

	/**
	 * Adds the trade listener to the merchant.
	 * 
	 * @param listener the listener
	 * @return true if not added before
	 */
	boolean addListener(MerchantTradeListener listener);

	/**
	 * Removes the trade listener to the merchant.
	 * 
	 * @param listener the listener
	 * @return true if added before
	 */
	boolean removeListener(MerchantTradeListener listener);

	/**
	 * Gets the trade listeners of the merchant.
	 * 
	 * @return the trade listeners
	 */
	Collection<MerchantTradeListener> getListeners();

	/**
	 * Adds a new offer to the merchant.
	 * 
	 * @param offer the offer
	 */
	void addOffer(MerchantOffer offer);

	/**
	 * Adds the offers to the merchant.
	 * 
	 * @param offers the offers
	 */
	void addOffers(Iterable<MerchantOffer> offers);

	/**
	 * Sorts all the offers of the merchant.
	 * 
	 * @param comparator the comparator
	 */
	void sortOffers(Comparator<MerchantOffer> comparator);

	/**
	 * Removes the offer from the merchant.
	 * 
	 * @param offer the offer
	 */
	void removeOffer(MerchantOffer offer);

	/**
	 * Removes the offers from the merchant.
	 * 
	 * @param offers the offers
	 */
	void removeOffers(Iterable<MerchantOffer> offers);

	/**
	 * Gets the offers of the merchant.
	 * 
	 * @return the offers
	 */
	List<MerchantOffer> getOffers();

	/**
	 * Gets the offer at the index.
	 * 
	 * @param index the index
	 * @return the offer
	 */
	MerchantOffer getOfferAt(int index);

	/**
	 * Gets the amount of offers in this merchant.
	 * 
	 * @return the count
	 */
	int getOffersCount();

	/**
	 * Sets the offer at the index.
	 * 
	 * @param index the index
	 * @param offer the offer
	 */
	void setOfferAt(int index, MerchantOffer offer);

	/**
	 * Inserts the offer at the index.
	 * 
	 * @param index the index
	 * @param offer the offer
	 */
	void insetOfferAt(int index, MerchantOffer offer);

	/**
	 * Adds a customer to the merchant.
	 * 
	 * @param player the player
	 * @return true if not a customer before
	 */
	boolean addCustomer(Player player);

	/**
	 * Removes a customer from the merchant.
	 * 
	 * @param player the player
	 * @return true if a customer before
	 */
	boolean removeCustomer(Player player);

	/**
	 * Gets whether the merchant the customer has.
	 * 
	 * @param player the player
	 * @return true if customer
	 */
	boolean hasCustomer(Player player);

	/**
	 * Gets the customers of the merchant.
	 * 
	 * @return the customers
	 */
	Collection<Player> getCustomers();

}
