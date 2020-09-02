// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class TargetUnselected extends ServerPacket
{
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    
    public TargetUnselected(final Creature character) {
        this._targetObjId = character.getObjectId();
        this._x = character.getX();
        this._y = character.getY();
        this._z = character.getZ();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TARGET_UNSELECTED);
        this.writeInt(this._targetObjId);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(0);
    }
}
