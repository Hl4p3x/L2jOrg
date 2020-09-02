// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;

public class PrivateStoreManageListBuy extends AbstractItemPacket
{
    private final int _sendType;
    private final int _objId;
    private final long _playerAdena;
    private final Collection<Item> _itemList;
    private final TradeItem[] _buyList;
    
    public PrivateStoreManageListBuy(final int sendType, final Player player) {
        this._sendType = sendType;
        this._objId = player.getObjectId();
        this._playerAdena = player.getAdena();
        this._itemList = player.getInventory().getUniqueItems(false);
        this._buyList = player.getBuyList().getItems();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PRIVATE_STORE_BUY_MANAGE_LIST);
        this.writeByte((byte)this._sendType);
        if (this._sendType == 2) {
            this.writeInt(this._itemList.size());
            this.writeInt(this._itemList.size());
            for (final Item item : this._itemList) {
                this.writeItem(item);
                this.writeLong(item.getTemplate().getReferencePrice() * 2L);
            }
        }
        else {
            this.writeInt(this._objId);
            this.writeLong(this._playerAdena);
            this.writeInt(0);
            for (final Item item : this._itemList) {
                this.writeItem(item);
                this.writeLong(item.getTemplate().getReferencePrice() * 2L);
            }
            this.writeInt(0);
            for (final TradeItem item2 : this._buyList) {
                this.writeItem(item2);
                this.writeLong(item2.getPrice());
                this.writeLong(item2.getItem().getReferencePrice() * 2L);
                this.writeLong(item2.getCount());
            }
        }
    }
}
