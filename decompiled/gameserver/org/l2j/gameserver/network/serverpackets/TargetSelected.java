// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class TargetSelected extends ServerPacket
{
    private final int _objectId;
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    
    public TargetSelected(final int objectId, final int targetId, final int x, final int y, final int z) {
        this._objectId = objectId;
        this._targetObjId = targetId;
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TARGET_SELECTED);
        this.writeInt(this._objectId);
        this.writeInt(this._targetObjId);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(0);
    }
}
