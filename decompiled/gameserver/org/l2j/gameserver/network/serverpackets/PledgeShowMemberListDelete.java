// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class PledgeShowMemberListDelete extends ServerPacket
{
    private final String _player;
    
    public PledgeShowMemberListDelete(final String playerName) {
        this._player = playerName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PLEDGE_SHOW_MEMBER_LIST_DELETE);
        this.writeString((CharSequence)this._player);
    }
}
