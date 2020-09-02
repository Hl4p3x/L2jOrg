// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class FriendAddRequest extends ServerPacket
{
    private final String _requestorName;
    
    public FriendAddRequest(final String requestorName) {
        this._requestorName = requestorName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.FRIEND_ADD_REQUEST);
        this.writeByte((byte)0);
        this.writeString((CharSequence)this._requestorName);
    }
}
