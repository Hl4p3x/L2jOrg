// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.item.instance.Item;

public final class GetItem extends ServerPacket
{
    private final Item _item;
    private final int _playerId;
    
    public GetItem(final Item item, final int playerId) {
        this._item = item;
        this._playerId = playerId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GET_ITEM);
        this.writeInt(this._playerId);
        this.writeInt(this._item.getObjectId());
        this.writeInt(this._item.getX());
        this.writeInt(this._item.getY());
        this.writeInt(this._item.getZ());
    }
}
