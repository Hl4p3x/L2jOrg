// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExRegenMax extends ServerPacket
{
    private final int _time;
    private final int _tickInterval;
    private final double _amountPerTick;
    
    public ExRegenMax(final int time, final int tickInterval, final double amountPerTick) {
        this._time = time;
        this._tickInterval = tickInterval;
        this._amountPerTick = amountPerTick;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_REGEN_MAX);
        this.writeInt(1);
        this.writeInt(this._time);
        this.writeInt(this._tickInterval);
        this.writeDouble(this._amountPerTick);
    }
}
