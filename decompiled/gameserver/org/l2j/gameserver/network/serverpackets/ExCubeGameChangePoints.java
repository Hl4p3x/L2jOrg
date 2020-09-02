// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExCubeGameChangePoints extends ServerPacket
{
    int _timeLeft;
    int _bluePoints;
    int _redPoints;
    
    public ExCubeGameChangePoints(final int timeLeft, final int bluePoints, final int redPoints) {
        this._timeLeft = timeLeft;
        this._bluePoints = bluePoints;
        this._redPoints = redPoints;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BLOCK_UPSET_STATE);
        this.writeInt(2);
        this.writeInt(this._timeLeft);
        this.writeInt(this._bluePoints);
        this.writeInt(this._redPoints);
    }
}
