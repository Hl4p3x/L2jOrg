// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExPVPMatchCCMyRecord extends ServerPacket
{
    private final int _points;
    
    public ExPVPMatchCCMyRecord(final int points) {
        this._points = points;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PVPMATCH_CC_MY_RECORD);
        this.writeInt(this._points);
    }
}
