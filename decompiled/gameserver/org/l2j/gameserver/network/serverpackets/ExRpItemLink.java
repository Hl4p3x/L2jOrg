// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;

public final class ExRpItemLink extends AbstractItemPacket
{
    private final Item _item;
    
    public ExRpItemLink(final Item item) {
        this._item = item;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RP_ITEM_LINK);
        this.writeItem(this._item);
    }
}
