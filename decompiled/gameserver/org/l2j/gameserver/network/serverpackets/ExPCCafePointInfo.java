// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPCCafePointInfo extends ServerPacket
{
    private final int _points;
    private final int _mAddPoint;
    private final int _mPeriodType;
    private final int _remainTime;
    private final int _pointType;
    private final int _time;
    
    public ExPCCafePointInfo() {
        this._points = 0;
        this._mAddPoint = 0;
        this._remainTime = 0;
        this._mPeriodType = 0;
        this._pointType = 0;
        this._time = 0;
    }
    
    public ExPCCafePointInfo(final int points, final int pointsToAdd, final int time) {
        this._points = points;
        this._mAddPoint = pointsToAdd;
        this._mPeriodType = 1;
        this._remainTime = 0;
        this._pointType = ((pointsToAdd < 0) ? 2 : 1);
        this._time = time;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PCCAFE_POINT_INFO);
        this.writeInt(this._points);
        this.writeInt(this._mAddPoint);
        this.writeByte((byte)this._mPeriodType);
        this.writeInt(this._remainTime);
        this.writeByte((byte)this._pointType);
        this.writeInt(this._time * 3);
    }
}
