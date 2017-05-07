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
package me.cybermaxke.merchants.v111r1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.netty.buffer.Unpooled;
import me.cybermaxke.merchants.api.Merchant;
import me.cybermaxke.merchants.api.MerchantOffer;
import me.cybermaxke.merchants.api.MerchantTradeListener;
import net.minecraft.server.v1_11_R1.*;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class SMerchant implements IMerchant, Merchant {

    // The trade handlers
    final Set<MerchantTradeListener> handlers = Sets.newHashSet();
    // The recipes list
    private final MerchantRecipeList offers = new MerchantRecipeList();
    // The customers
    private final Set<Player> customers = Sets.newHashSet();
    // Internal use only
    SMerchantOffer onTrade;
    EntityPlayer onTradePlayer;
    // The title of the merchant
    private String title;
    private boolean jsonTitle;
    // The title that will be send
    private IChatBaseComponent sendTitle;

    SMerchant(final String title, final boolean jsonTitle) {
        this.setTitle(title, jsonTitle);
    }

    @Override
    public String getTitle() {
        return this.title;
    }
    @Override
    public void setTitle(final String title) {
        setTitle(title, false);
    }
    @Override
    public boolean isTitleJson() {
        return this.jsonTitle;
    }
    @Override
    public void setTitle(final String title, final boolean jsonTitle) {
        checkNotNull(title, "title");

        // The old title
        final IChatBaseComponent oldTitle = this.sendTitle;
        final IChatBaseComponent newTitle;

        if (jsonTitle) {
            try {
                newTitle = ChatSerializer.a(title);
            } catch (final Exception e) {
                throw new IllegalArgumentException("invalid json format (" + title + ")", e);
            }
        }
        else {
            newTitle = CraftChatMessage.fromString(title)[0];
        }

        this.sendTitle = newTitle;
        this.jsonTitle = jsonTitle;
        this.title = title;

        // Send a update
        if (!this.sendTitle.equals(oldTitle)) {
            sendTitleUpdate();
        }
    }
    @Override
    public boolean addListener(final MerchantTradeListener listener) {
        checkNotNull(listener, "listener");
        return this.handlers.add(listener);
    }

    @Override
    public boolean removeListener(final MerchantTradeListener listener) {
        checkNotNull(listener, "listener");
        return this.handlers.remove(listener);
    }

    @Override
    public Collection<MerchantTradeListener> getListeners() {
        return Lists.newArrayList(this.handlers);
    }
    @Override
    public void addOffer(final MerchantOffer offer) {
        checkNotNull(offer, "offer");

        //noinspection SuspiciousMethodCalls
        if (this.offers.contains(offer)) {
            return;
        }

        // Add the offer
        this.offers.add((MerchantRecipe) offer);

        // Link the offer
        ((SMerchantOffer) offer).add(this);

        // Send the new offer list
        sendUpdate();
    }
    @Override
    public void addOffers(final Iterable<MerchantOffer> offers) {
        checkNotNull(offers, "offers");

        // Only update if necessary
        if (!offers.iterator().hasNext()) {
            return;
        }

        // Add and link the offers
        for (final MerchantOffer offer : offers) {
            //noinspection SuspiciousMethodCalls
            if (this.offers.contains(offer)) {
                continue;
            }
            this.offers.add((MerchantRecipe) offer);
            ((SMerchantOffer) offer).add(this);
        }

        // Send the new offer list
        sendUpdate();
    }
    @Override
    public void sortOffers(final Comparator<MerchantOffer> comparator) {
        checkNotNull(comparator, "comparator");

        // Only sort if necessary
        if (this.offers.size() <= 1) {
            return;
        }

        // Sort the offers
        Collections.sort(this.offers, new Comparator<MerchantRecipe>() {

            @Override
            public int compare(final MerchantRecipe arg0, final MerchantRecipe arg1) {
                return comparator.compare((MerchantOffer) arg0, (MerchantOffer) arg1);
            }

        });

        // Send the new offer list
        sendUpdate();
    }
    @Override
    public void removeOffer(final MerchantOffer offer) {
        checkNotNull(offer, "offer");

        //noinspection SuspiciousMethodCalls
        if (this.offers.remove(offer)) {
            // Unlink the offer
            ((SMerchantOffer) offer).remove(this);

            // Send the new offer list
            sendUpdate();
        }
    }
    @Override
    public void removeOffers(final Iterable<MerchantOffer> offers) {
        checkNotNull(offers, "offers");

        // Only update if necessary
        if (offers.iterator().hasNext()) {
            return;
        }

        //noinspection SuspiciousMethodCalls
        if (this.offers.removeAll(Lists.newArrayList(offers))) {
            // Unlink the offers
            for (final MerchantOffer offer : offers) {
                ((SMerchantOffer) offer).remove(this);
            }

            // Send the new offer list
            sendUpdate();
        }
    }
    @Override
    public List<MerchantOffer> getOffers() {
        final List<MerchantOffer> offers = Lists.newArrayList();
        for (final MerchantRecipe recipe : this.offers) {
            offers.add((MerchantOffer) recipe);
        }
        return offers;
    }
    @Override
    public MerchantOffer getOfferAt(final int index) {
        if (index < 0 || index >= this.offers.size()) {
            throw new IndexOutOfBoundsException(
                "index (" + index + ") out of bounds min (0) and max (" + this.offers.size() + ")");
        }

        return (MerchantOffer) this.offers.get(index);
    }
    @Override
    public int getOffersCount() {
        return this.offers.size();
    }
    @Override
    public void setOfferAt(final int index, final MerchantOffer offer) {
        checkNotNull(offer, "offer");

        if (index < 0 || index >= this.offers.size()) {
            throw new IndexOutOfBoundsException(
                "index (" + index + ") out of bounds min (0) and max (" + this.offers.size() + ")");
        }

        final SMerchantOffer old = (SMerchantOffer) this.offers.set(index, (MerchantRecipe) offer);
        old.remove(this);

        // Send the new offer list
        sendUpdate();
    }
    @Override
    public void insetOfferAt(final int index, final MerchantOffer offer) {
        checkNotNull(offer, "offer");

        if (index < 0 || index >= this.offers.size()) {
            throw new IndexOutOfBoundsException(
                "index (" + index + ") out of bounds min (0) and max (" + this.offers.size() + ")");
        }

        this.offers.add(index, (MerchantRecipe) offer);
    }
    @Override
    public boolean addCustomer(final Player player) {
        checkNotNull(player, "player");

        if (this.customers.add(player)) {
            final EntityPlayer player0 = ((CraftPlayer) player).getHandle();
            Container container0 = null;

            try {
                container0 = new SContainerMerchant(player0, this);
                container0 = CraftEventFactory.callInventoryOpenEvent(player0, container0);
            } catch (final Exception e) {
                e.printStackTrace();
            }

            if (container0 == null) {
                this.customers.remove(player);
                return false;
            }

            final int window = player0.nextContainerCounter();

            player0.activeContainer = container0;
            player0.activeContainer.windowId = window;
            player0.activeContainer.addSlotListener(player0);

            // Open the window
            player0.playerConnection
                .sendPacket(new PacketPlayOutOpenWindow(window, "minecraft:villager", this.sendTitle, 0));

            // Write the recipe list
            final PacketDataSerializer content = new PacketDataSerializer(Unpooled.buffer());
            content.writeInt(window);
            this.offers.a(content);

            // Send the offers
            player0.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", content));

            return true;
        }

        return false;
    }

    @Override
    public boolean removeCustomer(final Player player) {
        checkNotNull(player, "player");

        if (this.customers.remove(player)) {
            player.closeInventory();
            return true;
        }

        return false;
    }

    @Override
    public boolean hasCustomer(final Player player) {
        checkNotNull(player, "player");
        return this.customers.contains(player);
    }

    @Override
    public Collection<Player> getCustomers() {
        return Lists.newArrayList(this.customers);
    }
    private void sendTitleUpdate() {
        // Re-send the open window message to update the window name
        for (final Player customer : this.customers) {
            final EntityPlayer player0 = ((CraftPlayer) customer).getHandle();
            player0.playerConnection.sendPacket(
                new PacketPlayOutOpenWindow(player0.activeContainer.windowId, "minecraft:villager", this.sendTitle, 0));
            player0.updateInventory(player0.activeContainer);
        }
    }
    // Called when the merchant requires a update
    void sendUpdate() {
        if (this.customers.isEmpty()) {
            return;
        }
        // Only send if needed
        if (this.onTradePlayer != null && this.customers.size() <= 1) {
            return;
        }

        // Write the recipe list
        final PacketDataSerializer content0 = new PacketDataSerializer(Unpooled.buffer());
        this.offers.a(content0);

        // Send a packet to all the players
        for (final Player customer : this.customers) {
            final EntityPlayer player0 = ((CraftPlayer) customer).getHandle();

            // Only send to player that need it
            if (player0 == this.onTradePlayer) {
                continue;
            }

            // Every player has a different window id
            final PacketDataSerializer content1 = new PacketDataSerializer(Unpooled.buffer());
            content1.writeInt(player0.activeContainer.windowId);
            content1.writeBytes(content0);

            player0.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", content1));
        }
    }
    @Override
    public void setTradingPlayer(final EntityHuman human) {
        // Not used
    }
    @Override
    public EntityHuman getTrader() {
        // Not used
        return null;
    }
    @Override
    public MerchantRecipeList getOffers(final EntityHuman human) {
        return this.offers;
    }
    @Override
    public void a(final MerchantRecipe recipe) {
        // Used by the custom merchant result slot
        this.onTrade = (SMerchantOffer) recipe;
    }
    @Override
    public void a(final ItemStack itemStack) {
        // Not used
    }
    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return this.sendTitle;
    }
    @Override
    public World t_() {
        return MinecraftServer.getServer().getWorld();
    }
    @Override
    public BlockPosition u_() {
        return new BlockPosition(0, 0, 0);
    }
}