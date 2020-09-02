// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.PartyDistributionType;

public class AskJoinParty extends ServerPacket
{
    private final String _requestorName;
    private final PartyDistributionType _partyDistributionType;
    
    public AskJoinParty(final String requestorName, final PartyDistributionType partyDistributionType) {
        this._requestorName = requestorName;
        this._partyDistributionType = partyDistributionType;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.ASK_JOIN_PARTY);
        this.writeString((CharSequence)this._requestorName);
        this.writeInt(this._partyDistributionType.getId());
    }
}
