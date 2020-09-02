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

public class PrivateStoreManageListSell extends AbstractItemPacket
{
    private final int _sendType;
    private final int _objId;
    private final long _playerAdena;
    private final boolean _packageSale;
    private final Collection<TradeItem> _itemList;
    private final TradeItem[] _sellList;
    
    public PrivateStoreManageListSell(final int sendType, final Player player, final boolean isPackageSale) {
        this._sendType = sendType;
        this._objId = player.getObjectId();
        this._playerAdena = player.getAdena();
        player.getSellList().updateItems();
        this._packageSale = isPackageSale;
        this._itemList = player.getInventory().getAvailableItems(player.getSellList());
        this._sellList = player.getSellList().getItems();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PRIVATE_STORE_MANAGE_LIST);
        this.writeByte((byte)this._sendType);
        if (this._sendType == 2) {
            this.writeInt(this._itemList.size());
            this.writeInt(this._itemList.size());
            for (final TradeItem item : this._itemList) {
                this.writeItem(item);
                this.writeLong(item.getItem().getReferencePrice() * 2L);
            }
        }
        else {
            this.writeInt(this._objId);
            this.writeInt((int)(this._packageSale ? 1 : 0));
            this.writeLong(this._playerAdena);
            this.writeInt(0);
            for (final TradeItem item : this._itemList) {
                this.writeItem(item);
                this.writeLong(item.getItem().getReferencePrice() * 2L);
            }
            this.writeInt(0);
            for (final TradeItem item2 : this._sellList) {
                this.writeItem(item2);
                this.writeLong(item2.getPrice());
                this.writeLong(item2.getItem().getReferencePrice() * 2L);
            }
        }
    }
}
