// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExShuttleGetOn extends ServerPacket
{
    private final int _playerObjectId;
    private final int _shuttleObjectId;
    private final Location _pos;
    
    public ExShuttleGetOn(final Player player, final Shuttle shuttle) {
        this._playerObjectId = player.getObjectId();
        this._shuttleObjectId = shuttle.getObjectId();
        this._pos = player.getInVehiclePosition();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_GETON_SHUTTLE);
        this.writeInt(this._playerObjectId);
        this.writeInt(this._shuttleObjectId);
        this.writeInt(this._pos.getX());
        this.writeInt(this._pos.getY());
        this.writeInt(this._pos.getZ());
    }
}
