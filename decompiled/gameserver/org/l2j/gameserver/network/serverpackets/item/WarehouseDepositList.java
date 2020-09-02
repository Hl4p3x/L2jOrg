// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item;

import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.function.Function;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.item.container.Warehouse;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.container.WarehouseType;
import org.l2j.gameserver.model.actor.instance.Player;

public final class WarehouseDepositList
{
    public static void openOfPlayer(final Player player) {
        final Collection<Item> depositable;
        Util.doIfNonNull((Object)wareHouseOf(player, WarehouseType.PRIVATE), warehouse -> {
            depositable = player.getDepositableItems(WarehouseType.PRIVATE);
            player.sendPacket(header(warehouse, WarehouseType.PRIVATE, player, depositable.size()), new DepositableList(depositable));
        });
    }
    
    public static void openOfClan(final Player player) {
        final Collection<Item> depositable;
        Util.doIfNonNull((Object)wareHouseOf(player, WarehouseType.CLAN), warehouse -> {
            depositable = player.getDepositableItems(WarehouseType.CLAN);
            player.sendPacket(header(warehouse, WarehouseType.CLAN, player, depositable.size()), new DepositableList(depositable));
        });
    }
    
    private static Header header(final ItemContainer warehouse, final WarehouseType type, final Player player, final int depositableAmount) {
        final IntSet stackableDeposited = warehouse.getItemsId(Item::isStackable);
        return new Header(type, stackableDeposited, warehouse.getSize(), player.getAdena(), depositableAmount);
    }
    
    private static ItemContainer wareHouseOf(final Player player, final WarehouseType type) {
        ItemContainer itemContainer = null;
        switch (type) {
            case PRIVATE: {
                itemContainer = player.getWarehouse();
                break;
            }
            case CLAN: {
                itemContainer = (Warehouse)Util.computeIfNonNull((Object)player.getClan(), (Function)Clan::getWarehouse);
                break;
            }
            case FREIGHT: {
                itemContainer = player.getFreight();
                break;
            }
            default: {
                itemContainer = null;
                break;
            }
        }
        return itemContainer;
    }
    
    private static class Header extends ServerPacket
    {
        private final WarehouseType type;
        private final long adenaAmount;
        private final IntSet stackableDeposited;
        private final int depositableAmount;
        private final int depositedAmount;
        
        private Header(final WarehouseType type, final IntSet stackableDeposited, final int depositedAmount, final long adenaAmount, final int depositableAmount) {
            this.type = type;
            this.adenaAmount = adenaAmount;
            this.stackableDeposited = stackableDeposited;
            this.depositedAmount = depositedAmount;
            this.depositableAmount = depositableAmount;
        }
        
        @Override
        protected void writeImpl(final GameClient client) {
            this.writeId(ServerPacketId.WAREHOUSE_DEPOSIT_LIST);
            this.writeByte(ItemPacketType.HEADER.clientId());
            this.writeShort(this.type.clientId());
            this.writeLong(this.adenaAmount);
            this.writeInt(this.depositedAmount);
            this.writeShort(this.stackableDeposited.size());
            this.stackableDeposited.forEach(x$0 -> this.writeInt(x$0));
            this.writeInt(this.depositableAmount);
        }
    }
    
    private static class DepositableList extends AbstractItemPacket
    {
        private final Collection<Item> depositableItems;
        
        public DepositableList(final Collection<Item> depositableItems) {
            this.depositableItems = depositableItems;
        }
        
        @Override
        protected void writeImpl(final GameClient client) {
            this.writeId(ServerPacketId.WAREHOUSE_DEPOSIT_LIST);
            this.writeByte(ItemPacketType.LIST.clientId());
            this.writeInt(this.depositableItems.size());
            this.writeInt(this.depositableItems.size());
            for (final Item item : this.depositableItems) {
                this.writeItem(item, client.getPlayer());
                this.writeInt(item.getObjectId());
            }
        }
    }
}
