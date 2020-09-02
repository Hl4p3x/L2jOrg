// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Map;

public class ExGetBossRecord extends ServerPacket
{
    private final Map<Integer, Integer> _bossRecordInfo;
    private final int _ranking;
    private final int _totalPoints;
    
    public ExGetBossRecord(final int ranking, final int totalScore, final Map<Integer, Integer> list) {
        this._ranking = ranking;
        this._totalPoints = totalScore;
        this._bossRecordInfo = list;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_GET_BOSS_RECORD);
        this.writeInt(this._ranking);
        this.writeInt(this._totalPoints);
        if (this._bossRecordInfo == null) {
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
        }
        else {
            this.writeInt(this._bossRecordInfo.size());
            for (final Map.Entry<Integer, Integer> entry : this._bossRecordInfo.entrySet()) {
                this.writeInt((int)entry.getKey());
                this.writeInt((int)entry.getValue());
                this.writeInt(0);
            }
        }
    }
}
