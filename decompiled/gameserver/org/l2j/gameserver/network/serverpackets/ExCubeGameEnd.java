// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExCubeGameEnd extends ServerPacket
{
    boolean _isRedTeamWin;
    
    public ExCubeGameEnd(final boolean isRedTeamWin) {
        this._isRedTeamWin = isRedTeamWin;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BLOCK_UPSET_STATE);
        this.writeInt(1);
        this.writeInt((int)(this._isRedTeamWin ? 1 : 0));
        this.writeInt(0);
    }
}
