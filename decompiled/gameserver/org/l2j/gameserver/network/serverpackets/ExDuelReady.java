// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExDuelReady extends ServerPacket
{
    public static final ExDuelReady PLAYER_DUEL;
    public static final ExDuelReady PARTY_DUEL;
    private final int _partyDuel;
    
    public ExDuelReady(final boolean isPartyDuel) {
        this._partyDuel = (isPartyDuel ? 1 : 0);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DUEL_READY);
        this.writeInt(this._partyDuel);
    }
    
    static {
        PLAYER_DUEL = new ExDuelReady(false);
        PARTY_DUEL = new ExDuelReady(true);
    }
}
