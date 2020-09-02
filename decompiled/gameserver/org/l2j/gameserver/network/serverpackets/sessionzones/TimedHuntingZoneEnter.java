// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.sessionzones;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class TimedHuntingZoneEnter extends ServerPacket
{
    private final int _remainingTime;
    
    public TimedHuntingZoneEnter(final int remainingTime) {
        this._remainingTime = remainingTime;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_USER_ENTER);
        this.writeInt(this._remainingTime);
    }
}
