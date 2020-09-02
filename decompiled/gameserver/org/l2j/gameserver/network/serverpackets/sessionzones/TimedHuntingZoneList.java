// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.sessionzones;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class TimedHuntingZoneList extends ServerPacket
{
    private final Player _player;
    private final boolean _isInTimedHuntingZone;
    
    public TimedHuntingZoneList(final Player player) {
        this._player = player;
        this._isInTimedHuntingZone = player.isInTimedHuntingZone();
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_LIST);
        final long currentTime = System.currentTimeMillis();
        this.writeInt(1);
        this.writeInt(1);
        this.writeInt(57);
        this.writeLong(Config.TIME_LIMITED_ZONE_TELEPORT_FEE);
        this.writeInt(1);
        this.writeInt(2);
        this.writeInt(78);
        this.writeInt(999);
        this.writeInt(0);
        long endTime = this._player.getHuntingZoneResetTime(2);
        if (endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY < currentTime) {
            endTime = currentTime + 3600000L;
        }
        this.writeInt((int)Math.max(endTime - currentTime, 0L) / 1000);
        this.writeInt((int)(Config.TIME_LIMITED_MAX_ADDED_TIME / 1000L));
        this.writeInt(3600);
        this.writeInt(3600);
        this.writeByte((int)(this._isInTimedHuntingZone ? 0 : 1));
        this.writeByte(false);
        this.writeByte(true);
    }
}
