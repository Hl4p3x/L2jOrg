// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class SetSummonRemainTime extends ServerPacket
{
    private final int _maxTime;
    private final int _remainingTime;
    
    public SetSummonRemainTime(final int maxTime, final int remainingTime) {
        this._remainingTime = remainingTime;
        this._maxTime = maxTime;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SET_SUMMON_REMAIN_TIME);
        this.writeInt(this._maxTime);
        this.writeInt(this._remainingTime);
    }
}
