// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public final class MoveToLocation extends ServerPacket
{
    private final int _charObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _xDst;
    private final int _yDst;
    private final int _zDst;
    
    public MoveToLocation(final Creature cha) {
        this._charObjId = cha.getObjectId();
        this._x = cha.getX();
        this._y = cha.getY();
        this._z = cha.getZ();
        this._xDst = cha.getXdestination();
        this._yDst = cha.getYdestination();
        this._zDst = cha.getZdestination();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MOVE_TO_LOCATION);
        this.writeInt(this._charObjId);
        this.writeInt(this._xDst);
        this.writeInt(this._yDst);
        this.writeInt(this._zDst);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
    }
}
