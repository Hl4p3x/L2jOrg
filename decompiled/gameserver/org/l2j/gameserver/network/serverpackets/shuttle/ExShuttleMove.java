// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExShuttleMove extends ServerPacket
{
    private final Shuttle _shuttle;
    private final int _x;
    private final int _y;
    private final int _z;
    
    public ExShuttleMove(final Shuttle shuttle, final int x, final int y, final int z) {
        this._shuttle = shuttle;
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHUTTLE_MOVE);
        this.writeInt(this._shuttle.getObjectId());
        this.writeInt((int)this._shuttle.getStats().getMoveSpeed());
        this.writeInt((int)this._shuttle.getStats().getRotationSpeed());
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
    }
}
