// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExBrLoadEventTopRankers extends ServerPacket
{
    private final int _eventId;
    private final int _day;
    private final int _count;
    private final int _bestScore;
    private final int _myScore;
    
    public ExBrLoadEventTopRankers(final int eventId, final int day, final int count, final int bestScore, final int myScore) {
        this._eventId = eventId;
        this._day = day;
        this._count = count;
        this._bestScore = bestScore;
        this._myScore = myScore;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_LOAD_EVENT_TOP_RANKERS_ACK);
        this.writeInt(this._eventId);
        this.writeInt(this._day);
        this.writeInt(this._count);
        this.writeInt(this._bestScore);
        this.writeInt(this._myScore);
    }
}
