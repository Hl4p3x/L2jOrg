// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class ChooseInventoryItem extends ServerPacket
{
    private final int _itemId;
    
    public ChooseInventoryItem(final int itemId) {
        this._itemId = itemId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHOOSE_INVENTORY_ITEM);
        this.writeInt(this._itemId);
    }
}
