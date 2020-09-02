// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExDuelStart extends ServerPacket
{
    public static final ExDuelStart PLAYER_DUEL;
    public static final ExDuelStart PARTY_DUEL;
    private final int _partyDuel;
    
    public ExDuelStart(final boolean isPartyDuel) {
        this._partyDuel = (isPartyDuel ? 1 : 0);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DUEL_START);
        this.writeInt(this._partyDuel);
    }
    
    static {
        PLAYER_DUEL = new ExDuelStart(false);
        PARTY_DUEL = new ExDuelStart(true);
    }
}
