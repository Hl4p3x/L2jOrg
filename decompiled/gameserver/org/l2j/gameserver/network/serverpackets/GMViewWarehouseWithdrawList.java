// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;

public class GMViewWarehouseWithdrawList extends AbstractItemPacket
{
    private final Collection<Item> _items;
    private final String playerName;
    private final long _money;
    private final int sendType;
    
    public GMViewWarehouseWithdrawList(final int sendType, final Player cha) {
        this.sendType = sendType;
        this._items = cha.getWarehouse().getItems();
        this.playerName = cha.getName();
        this._money = cha.getWarehouse().getAdena();
    }
    
    public GMViewWarehouseWithdrawList(final int sendType, final Clan clan) {
        this.sendType = sendType;
        this.playerName = clan.getLeaderName();
        this._items = clan.getWarehouse().getItems();
        this._money = clan.getWarehouse().getAdena();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GM_VIEW_WAREHOUSE_WITHDRAW_LIST);
        this.writeByte(this.sendType);
        if (this.sendType == 2) {
            this.writeInt(this._items.size());
            this.writeInt(this._items.size());
            for (final Item item : this._items) {
                this.writeItem(item);
                this.writeInt(item.getObjectId());
            }
        }
        else {
            this.writeString((CharSequence)this.playerName);
            this.writeLong(this._money);
            this.writeInt((int)(short)this._items.size());
        }
    }
}
