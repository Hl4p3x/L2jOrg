// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.Location;

public class ExTeleportToLocationActivate extends ServerPacket
{
    private final int _objectId;
    private final Location _loc;
    
    public ExTeleportToLocationActivate(final Creature character) {
        this._objectId = character.getObjectId();
        this._loc = character.getLocation();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_TELEPORT_TO_LOCATION_ACTIVATE);
        this.writeInt(this._objectId);
        this.writeInt(this._loc.getX());
        this.writeInt(this._loc.getY());
        this.writeInt(this._loc.getZ());
        this.writeInt(0);
        this.writeInt(this._loc.getHeading());
        this.writeInt(0);
    }
}
