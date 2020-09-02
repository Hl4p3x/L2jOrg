// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Boat;

public class VehicleInfo extends ServerPacket
{
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    
    public VehicleInfo(final Boat boat) {
        this._objId = boat.getObjectId();
        this._x = boat.getX();
        this._y = boat.getY();
        this._z = boat.getZ();
        this._heading = boat.getHeading();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.VEHICLE_INFO);
        this.writeInt(this._objId);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(this._heading);
    }
}
