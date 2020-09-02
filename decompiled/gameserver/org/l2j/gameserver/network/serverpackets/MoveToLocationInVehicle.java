// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Location;

public class MoveToLocationInVehicle extends ServerPacket
{
    private final int _charObjId;
    private final int _boatId;
    private final Location _destination;
    private final Location _origin;
    
    public MoveToLocationInVehicle(final Player player, final Location destination, final Location origin) {
        this._charObjId = player.getObjectId();
        this._boatId = player.getBoat().getObjectId();
        this._destination = destination;
        this._origin = origin;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.MOVE_TO_LOCATION_IN_VEHICLE);
        this.writeInt(this._charObjId);
        this.writeInt(this._boatId);
        this.writeInt(this._destination.getX());
        this.writeInt(this._destination.getY());
        this.writeInt(this._destination.getZ());
        this.writeInt(this._origin.getX());
        this.writeInt(this._origin.getY());
        this.writeInt(this._origin.getZ());
    }
}
