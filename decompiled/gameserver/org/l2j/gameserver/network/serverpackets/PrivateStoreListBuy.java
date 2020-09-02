// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.TradeItem;
import java.util.Collection;

public class PrivateStoreListBuy extends AbstractItemPacket
{
    private final int _objId;
    private final long _playerAdena;
    private final Collection<TradeItem> _items;
    
    public PrivateStoreListBuy(final Player player, final Player storePlayer) {
        this._objId = storePlayer.getObjectId();
        this._playerAdena = player.getAdena();
        storePlayer.getSellList().updateItems();
        this._items = storePlayer.getBuyList().getAvailableItems(player.getInventory());
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PRIVATE_STORE_BUY_LIST);
        this.writeInt(this._objId);
        this.writeLong(this._playerAdena);
        this.writeInt(0);
        this.writeInt(this._items.size());
        int slotNumber = 0;
        for (final TradeItem item : this._items) {
            ++slotNumber;
            this.writeItem(item);
            this.writeInt(slotNumber);
            this.writeLong(item.getPrice());
            this.writeLong(item.getItem().getReferencePrice() * 2L);
            this.writeLong(item.getStoreCount());
        }
    }
}
