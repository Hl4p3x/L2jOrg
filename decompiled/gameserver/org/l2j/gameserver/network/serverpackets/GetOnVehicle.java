// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Location;

public class GetOnVehicle extends ServerPacket
{
    private final int _charObjId;
    private final int _boatObjId;
    private final Location _pos;
    
    public GetOnVehicle(final int charObjId, final int boatObjId, final Location pos) {
        this._charObjId = charObjId;
        this._boatObjId = boatObjId;
        this._pos = pos;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GETON_VEHICLE);
        this.writeInt(this._charObjId);
        this.writeInt(this._boatObjId);
        this.writeInt(this._pos.getX());
        this.writeInt(this._pos.getY());
        this.writeInt(this._pos.getZ());
    }
}
