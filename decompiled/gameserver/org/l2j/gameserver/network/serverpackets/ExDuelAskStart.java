// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExDuelAskStart extends ServerPacket
{
    private final String _requestorName;
    private final int _partyDuel;
    
    public ExDuelAskStart(final String requestor, final int partyDuel) {
        this._requestorName = requestor;
        this._partyDuel = partyDuel;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DUEL_ASK_START);
        this.writeString((CharSequence)this._requestorName);
        this.writeInt(this._partyDuel);
    }
}
