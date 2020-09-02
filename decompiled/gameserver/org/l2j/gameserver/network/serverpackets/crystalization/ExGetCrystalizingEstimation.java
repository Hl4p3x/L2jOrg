// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.crystalization;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExGetCrystalizingEstimation extends ServerPacket
{
    private final List<ItemChanceHolder> _items;
    
    public ExGetCrystalizingEstimation(final List<ItemChanceHolder> items) {
        this._items = items;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_CRYSTALITEM_INFO);
        this.writeInt(this._items.size());
        for (final ItemChanceHolder holder : this._items) {
            this.writeInt(holder.getId());
            this.writeLong(holder.getCount());
            this.writeDouble(holder.getChance());
        }
    }
}
