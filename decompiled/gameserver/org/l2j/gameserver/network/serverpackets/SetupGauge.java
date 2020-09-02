// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class SetupGauge extends ServerPacket
{
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int CYAN = 2;
    private final int _dat1;
    private final int _time;
    private final int _time2;
    private final int _charObjId;
    
    public SetupGauge(final int objectId, final int dat1, final int time) {
        this._charObjId = objectId;
        this._dat1 = dat1;
        this._time = time;
        this._time2 = time;
    }
    
    public SetupGauge(final int objectId, final int color, final int currentTime, final int maxTime) {
        this._charObjId = objectId;
        this._dat1 = color;
        this._time = currentTime;
        this._time2 = maxTime;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SETUP_GAUGE);
        this.writeInt(this._charObjId);
        this.writeInt(this._dat1);
        this.writeInt(this._time);
        this.writeInt(this._time2);
    }
}
