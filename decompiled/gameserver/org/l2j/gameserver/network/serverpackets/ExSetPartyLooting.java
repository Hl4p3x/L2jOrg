// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.PartyDistributionType;

public class ExSetPartyLooting extends ServerPacket
{
    private final int _result;
    private final PartyDistributionType _partyDistributionType;
    
    public ExSetPartyLooting(final int result, final PartyDistributionType partyDistributionType) {
        this._result = result;
        this._partyDistributionType = partyDistributionType;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SET_PARTY_LOOTING);
        this.writeInt(this._result);
        this.writeInt(this._partyDistributionType.getId());
    }
}
