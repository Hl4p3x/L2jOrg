// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class ChangeWaitType extends ServerPacket
{
    public static final int WT_SITTING = 0;
    public static final int WT_STANDING = 1;
    public static final int WT_START_FAKEDEATH = 2;
    public static final int WT_STOP_FAKEDEATH = 3;
    private final int _charObjId;
    private final int _moveType;
    private final int _x;
    private final int _y;
    private final int _z;
    
    public ChangeWaitType(final Creature character, final int newMoveType) {
        this._charObjId = character.getObjectId();
        this._moveType = newMoveType;
        this._x = character.getX();
        this._y = character.getY();
        this._z = character.getZ();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHANGE_WAIT_TYPE);
        this.writeInt(this._charObjId);
        this.writeInt(this._moveType);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
    }
}
