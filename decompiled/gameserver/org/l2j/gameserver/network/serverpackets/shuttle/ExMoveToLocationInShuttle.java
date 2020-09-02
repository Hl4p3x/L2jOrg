// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExMoveToLocationInShuttle extends ServerPacket
{
    private final int _charObjId;
    private final int _airShipId;
    private final int _targetX;
    private final int _targetY;
    private final int _targetZ;
    private final int _fromX;
    private final int _fromY;
    private final int _fromZ;
    
    public ExMoveToLocationInShuttle(final Player player, final int fromX, final int fromY, final int fromZ) {
        this._charObjId = player.getObjectId();
        this._airShipId = player.getShuttle().getObjectId();
        this._targetX = player.getInVehiclePosition().getX();
        this._targetY = player.getInVehiclePosition().getY();
        this._targetZ = player.getInVehiclePosition().getZ();
        this._fromX = fromX;
        this._fromY = fromY;
        this._fromZ = fromZ;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MOVE_TO_LOCATION_IN_SHUTTLE);
        this.writeInt(this._charObjId);
        this.writeInt(this._airShipId);
        this.writeInt(this._targetX);
        this.writeInt(this._targetY);
        this.writeInt(this._targetZ);
        this.writeInt(this._fromX);
        this.writeInt(this._fromY);
        this.writeInt(this._fromZ);
    }
}
