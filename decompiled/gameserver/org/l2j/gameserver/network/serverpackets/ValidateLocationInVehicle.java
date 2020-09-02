// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Location;

public class ValidateLocationInVehicle extends ServerPacket
{
    private final int _charObjId;
    private final int _boatObjId;
    private final int _heading;
    private final Location _pos;
    
    public ValidateLocationInVehicle(final Player player) {
        this._charObjId = player.getObjectId();
        this._boatObjId = player.getBoat().getObjectId();
        this._heading = player.getHeading();
        this._pos = player.getInVehiclePosition();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.VALIDATE_LOCATION_IN_VEHICLE);
        this.writeInt(this._charObjId);
        this.writeInt(this._boatObjId);
        this.writeInt(this._pos.getX());
        this.writeInt(this._pos.getY());
        this.writeInt(this._pos.getZ());
        this.writeInt(this._heading);
    }
}
