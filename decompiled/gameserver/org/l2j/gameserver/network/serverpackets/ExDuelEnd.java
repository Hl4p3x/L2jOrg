// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExDuelEnd extends ServerPacket
{
    public static final ExDuelEnd PLAYER_DUEL;
    public static final ExDuelEnd PARTY_DUEL;
    private final int _partyDuel;
    
    public ExDuelEnd(final boolean isPartyDuel) {
        this._partyDuel = (isPartyDuel ? 1 : 0);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DUEL_END);
        this.writeInt(this._partyDuel);
    }
    
    static {
        PLAYER_DUEL = new ExDuelEnd(false);
        PARTY_DUEL = new ExDuelEnd(true);
    }
}
