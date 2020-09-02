// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.commission;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

public class ExResponseCommissionItemList extends AbstractItemPacket
{
    private final int sendType;
    private final Collection<Item> items;
    
    public ExResponseCommissionItemList(final int sendType, final Collection<Item> items) {
        this.sendType = sendType;
        this.items = items;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_COMMISSION_ITEM_LIST);
        this.writeByte((byte)this.sendType);
        if (this.sendType == 2) {
            this.writeInt(this.items.size());
            this.writeInt(this.items.size());
            for (final Item itemInstance : this.items) {
                this.writeItem(itemInstance);
            }
        }
        else {
            this.writeInt(0);
            this.writeInt(0);
        }
    }
}
