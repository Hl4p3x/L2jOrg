// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public final class StopMove extends ServerPacket
{
    private final int _objectId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    
    public StopMove(final Creature cha) {
        this(cha.getObjectId(), cha.getX(), cha.getY(), cha.getZ(), cha.getHeading());
    }
    
    public StopMove(final int objectId, final int x, final int y, final int z, final int heading) {
        this._objectId = objectId;
        this._x = x;
        this._y = y;
        this._z = z;
        this._heading = heading;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.STOP_MOVE);
        this.writeInt(this._objectId);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(this._heading);
    }
}
