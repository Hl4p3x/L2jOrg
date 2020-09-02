// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExStopMoveInShuttle extends ServerPacket
{
    private final int _charObjId;
    private final int _boatId;
    private final Location _pos;
    private final int _heading;
    
    public ExStopMoveInShuttle(final Player player, final int boatId) {
        this._charObjId = player.getObjectId();
        this._boatId = boatId;
        this._pos = player.getInVehiclePosition();
        this._heading = player.getHeading();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_STOP_MOVE_LOCATION_IN_SHUTTLE);
        this.writeInt(this._charObjId);
        this.writeInt(this._boatId);
        this.writeInt(this._pos.getX());
        this.writeInt(this._pos.getY());
        this.writeInt(this._pos.getZ());
        this.writeInt(this._heading);
    }
}
