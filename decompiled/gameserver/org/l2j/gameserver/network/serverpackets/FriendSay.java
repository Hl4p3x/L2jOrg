// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class FriendSay extends ServerPacket
{
    private final String _sender;
    private final String _receiver;
    private final String _message;
    
    public FriendSay(final String sender, final String reciever, final String message) {
        this._sender = sender;
        this._receiver = reciever;
        this._message = message;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.L2_FRIEND_SAY);
        this.writeInt(0);
        this.writeString((CharSequence)this._receiver);
        this.writeString((CharSequence)this._sender);
        this.writeString((CharSequence)this._message);
    }
}
