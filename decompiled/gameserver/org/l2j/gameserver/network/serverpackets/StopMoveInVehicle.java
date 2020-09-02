// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Location;

public class StopMoveInVehicle extends ServerPacket
{
    private final int _charObjId;
    private final int _boatId;
    private final Location _pos;
    private final int _heading;
    
    public StopMoveInVehicle(final Player player, final int boatId) {
        this._charObjId = player.getObjectId();
        this._boatId = boatId;
        this._pos = player.getInVehiclePosition();
        this._heading = player.getHeading();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.STOP_MOVE_IN_VEHICLE);
        this.writeInt(this._charObjId);
        this.writeInt(this._boatId);
        this.writeInt(this._pos.getX());
        this.writeInt(this._pos.getY());
        this.writeInt(this._pos.getZ());
        this.writeInt(this._heading);
    }
}
