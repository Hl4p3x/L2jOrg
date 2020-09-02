// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExShuttleGetOff extends ServerPacket
{
    private final int _playerObjectId;
    private final int _shuttleObjectId;
    private final int _x;
    private final int _y;
    private final int _z;
    
    public ExShuttleGetOff(final Player player, final Shuttle shuttle, final int x, final int y, final int z) {
        this._playerObjectId = player.getObjectId();
        this._shuttleObjectId = shuttle.getObjectId();
        this._x = x;
        this._y = y;
        this._z = z;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_GETOFF_SHUTTLE);
        this.writeInt(this._playerObjectId);
        this.writeInt(this._shuttleObjectId);
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
    }
}
