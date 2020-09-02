// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class FriendAddRequest extends ServerPacket
{
    private final String _requestorName;
    
    public FriendAddRequest(final String requestorName) {
        this._requestorName = requestorName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.FRIEND_ADD_REQUEST);
        this.writeByte((byte)1);
        this.writeString((CharSequence)this._requestorName);
    }
}
