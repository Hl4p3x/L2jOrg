// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class FriendRemove extends ServerPacket
{
    private final int _responce;
    private final String _charName;
    
    public FriendRemove(final String charName, final int responce) {
        this._responce = responce;
        this._charName = charName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.FRIEND_REMOVE);
        this.writeInt(this._responce);
        this.writeString((CharSequence)this._charName);
    }
}
