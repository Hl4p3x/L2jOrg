// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.WorldObject;

public final class TeleportToLocation extends ServerPacket
{
    private final int _targetObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    
    public TeleportToLocation(final WorldObject obj, final int x, final int y, final int z, final int heading) {
        this._targetObjId = obj.getObjectId();
        this._x = x;
        this._y = y;
        this._z = z;
        this._heading = heading;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TELEPORT_TO_LOCATION);
        this.writeInt(this._targetObjId);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(0);
        this.writeInt(this._heading);
        this.writeInt(0);
    }
}
