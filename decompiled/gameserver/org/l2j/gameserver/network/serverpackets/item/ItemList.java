// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item;

import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Collection;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.instance.Player;

public final class ItemList
{
    public static void show(final Player player) {
        sendPackets(player, true);
    }
    
    public static void sendList(final Player player) {
        sendPackets(player, false);
    }
    
    private static void sendPackets(final Player player, final boolean show) {
        final Collection<Item> items = player.getInventory().getItems(item -> !item.isQuestItem(), (Predicate<Item>[])new Predicate[0]);
        sendHeader(player, show, items.size());
        sendList(player, items);
    }
    
    private static void sendList(final Player player, final Collection<Item> items) {
        player.sendPacket(new List(items));
    }
    
    private static void sendHeader(final Player player, final boolean show, final int itemsAmount) {
        player.sendPacket(new Header(show, itemsAmount));
    }
    
    private static class Header extends ServerPacket
    {
        private final boolean show;
        private final int itemsAmount;
        
        public Header(final boolean show, final int itemsAmount) {
            this.show = show;
            this.itemsAmount = itemsAmount;
        }
        
        @Override
        protected void writeImpl(final GameClient client) {
            this.writeId(ServerPacketId.ITEMLIST);
            this.writeByte(ItemPacketType.HEADER.clientId());
            this.writeShort(this.show);
            this.writeShort(0);
            this.writeInt(this.itemsAmount);
        }
    }
    
    private static final class List extends AbstractItemPacket
    {
        private final Collection<Item> items;
        
        private List(final Collection<Item> items) {
            this.items = items;
        }
        
        @Override
        protected void writeImpl(final GameClient client) {
            this.writeId(ServerPacketId.ITEMLIST);
            this.writeByte(ItemPacketType.LIST.clientId());
            this.writeInt(this.items.size());
            this.writeInt(this.items.size());
            final Player player = client.getPlayer();
            for (final Item item : this.items) {
                this.writeItem(item, player);
            }
        }
    }
}
