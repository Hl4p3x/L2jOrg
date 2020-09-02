// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.WorldObject;

public final class Revive extends ServerPacket
{
    private final int _objectId;
    
    public Revive(final WorldObject obj) {
        this._objectId = obj.getObjectId();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.REVIVE);
        this.writeInt(this._objectId);
    }
}
