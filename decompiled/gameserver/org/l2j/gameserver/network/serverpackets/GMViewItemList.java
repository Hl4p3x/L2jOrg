// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;

public class GMViewItemList extends AbstractItemPacket
{
    private final int sendType;
    private final Collection<Item> items;
    private final int _limit;
    private final String playerName;
    
    public GMViewItemList(final int sendType, final Player cha) {
        this.sendType = sendType;
        this.playerName = cha.getName();
        this._limit = cha.getInventoryLimit();
        this.items = cha.getInventory().getItems();
    }
    
    public GMViewItemList(final int sendType, final Pet cha) {
        this.sendType = sendType;
        this.playerName = cha.getName();
        this._limit = cha.getInventoryLimit();
        this.items = cha.getInventory().getItems();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GM_VIEW_ITEMLIST);
        this.writeByte((byte)this.sendType);
        if (this.sendType == 2) {
            this.writeInt(this.items.size());
        }
        else {
            this.writeString((CharSequence)this.playerName);
            this.writeInt(this._limit);
        }
        this.writeInt(this.items.size());
        for (final Item item : this.items) {
            this.writeItem(item);
        }
    }
}
