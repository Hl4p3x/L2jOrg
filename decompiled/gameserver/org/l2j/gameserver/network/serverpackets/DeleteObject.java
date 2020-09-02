// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.WorldObject;

public final class DeleteObject extends ServerPacket
{
    private final int _objectId;
    
    public DeleteObject(final WorldObject obj) {
        this._objectId = obj.getObjectId();
    }
    
    public DeleteObject(final int objectId) {
        this._objectId = objectId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.DELETE_OBJECT);
        this.writeInt(this._objectId);
        this.writeByte((byte)0);
    }
}
