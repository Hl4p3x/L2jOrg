// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class VehicleStarted extends ServerPacket
{
    private final int _objectId;
    private final int _state;
    
    public VehicleStarted(final Creature boat, final int state) {
        this._objectId = boat.getObjectId();
        this._state = state;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.VEHICLE_START_PACKET);
        this.writeInt(this._objectId);
        this.writeInt(this._state);
    }
}
