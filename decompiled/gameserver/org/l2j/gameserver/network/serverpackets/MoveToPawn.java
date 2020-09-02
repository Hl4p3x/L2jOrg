// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

public class MoveToPawn extends ServerPacket
{
    private final int _charObjId;
    private final int _targetId;
    private final int _distance;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _tx;
    private final int _ty;
    private final int _tz;
    
    public MoveToPawn(final Creature cha, final WorldObject target, final int distance) {
        this._charObjId = cha.getObjectId();
        this._targetId = target.getObjectId();
        this._distance = distance;
        this._x = cha.getX();
        this._y = cha.getY();
        this._z = cha.getZ();
        this._tx = target.getX();
        this._ty = target.getY();
        this._tz = target.getZ();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MOVE_TO_PAWN);
        this.writeInt(this._charObjId);
        this.writeInt(this._targetId);
        this.writeInt(this._distance);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(this._tx);
        this.writeInt(this._ty);
        this.writeInt(this._tz);
    }
}
