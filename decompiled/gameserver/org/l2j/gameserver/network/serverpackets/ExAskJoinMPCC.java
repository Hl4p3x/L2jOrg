// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExAskJoinMPCC extends ServerPacket
{
    private final String _requestorName;
    
    public ExAskJoinMPCC(final String requestorName) {
        this._requestorName = requestorName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ASK_JOIN_MPCC);
        this.writeString((CharSequence)this._requestorName);
        this.writeInt(0);
    }
}
