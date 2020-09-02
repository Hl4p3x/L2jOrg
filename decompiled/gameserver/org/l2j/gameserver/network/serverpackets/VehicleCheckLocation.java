// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class VehicleCheckLocation extends ServerPacket
{
    private final Creature _boat;
    
    public VehicleCheckLocation(final Creature boat) {
        this._boat = boat;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.VEHICLE_CHECK_LOCATION);
        this.writeInt(this._boat.getObjectId());
        this.writeInt(this._boat.getX());
        this.writeInt(this._boat.getY());
        this.writeInt(this._boat.getZ());
        this.writeInt(this._boat.getHeading());
    }
}
