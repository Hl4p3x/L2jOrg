// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.PartyDistributionType;

public class ExAskModifyPartyLooting extends ServerPacket
{
    private final String _requestor;
    private final PartyDistributionType _partyDistributionType;
    
    public ExAskModifyPartyLooting(final String name, final PartyDistributionType partyDistributionType) {
        this._requestor = name;
        this._partyDistributionType = partyDistributionType;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ASK_MODIFY_PARTY_LOOTING);
        this.writeString((CharSequence)this._requestor);
        this.writeInt(this._partyDistributionType.getId());
    }
}
