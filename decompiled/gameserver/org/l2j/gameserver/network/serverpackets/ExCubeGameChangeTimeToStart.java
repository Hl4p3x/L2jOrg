// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExCubeGameChangeTimeToStart extends ServerPacket
{
    int _seconds;
    
    public ExCubeGameChangeTimeToStart(final int seconds) {
        this._seconds = seconds;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BLOCK_UPSET_LIST);
        this.writeInt(3);
        this.writeInt(this._seconds);
    }
}
