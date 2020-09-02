// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExAskJoinPartyRoom extends ServerPacket
{
    private final String _charName;
    private final String _roomName;
    
    public ExAskJoinPartyRoom(final Player player) {
        this._charName = player.getName();
        this._roomName = player.getMatchingRoom().getTitle();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ASK_JOIN_PARTY_ROOM);
        this.writeString((CharSequence)this._charName);
        this.writeString((CharSequence)this._roomName);
    }
}
