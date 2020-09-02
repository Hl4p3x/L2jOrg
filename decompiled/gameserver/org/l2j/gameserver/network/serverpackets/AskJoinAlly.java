// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class AskJoinAlly extends ServerPacket
{
    private final String _requestorName;
    private final int _requestorObjId;
    
    public AskJoinAlly(final int requestorObjId, final String requestorName) {
        this._requestorName = requestorName;
        this._requestorObjId = requestorObjId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ASK_JOIN_ALLIANCE);
        this.writeInt(this._requestorObjId);
        this.writeString((CharSequence)null);
        this.writeString((CharSequence)null);
        this.writeString((CharSequence)this._requestorName);
    }
}
