// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Boat;

public class VehicleDeparture extends ServerPacket
{
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _moveSpeed;
    private final int _rotationSpeed;
    
    public VehicleDeparture(final Boat boat) {
        this._objId = boat.getObjectId();
        this._x = boat.getXdestination();
        this._y = boat.getYdestination();
        this._z = boat.getZdestination();
        this._moveSpeed = (int)boat.getMoveSpeed();
        this._rotationSpeed = (int)boat.getStats().getRotationSpeed();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.VEHICLE_DEPARTURE);
        this.writeInt(this._objId);
        this.writeInt(this._moveSpeed);
        this.writeInt(this._rotationSpeed);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
    }
}
