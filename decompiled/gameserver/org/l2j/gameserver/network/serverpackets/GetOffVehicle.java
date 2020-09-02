// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class GetOffVehicle extends ServerPacket
{
    private final int _charObjId;
    private final int _boatObjId;
    private final int _x;
    private final int _y;
    private final int _z;
    
    public GetOffVehicle(final int charObjId, final int boatObjId, final int x, final int y, final int z) {
        this._charObjId = charObjId;
        this._boatObjId = boatObjId;
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GETOFF_VEHICLE);
        this.writeInt(this._charObjId);
        this.writeInt(this._boatObjId);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
    }
}
