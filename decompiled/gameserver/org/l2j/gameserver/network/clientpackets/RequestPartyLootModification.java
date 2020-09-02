// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.GameClient;

public class RequestPartyLootModification extends ClientPacket
{
    private int _partyDistributionTypeId;
    
    public void readImpl() {
        this._partyDistributionTypeId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final PartyDistributionType partyDistributionType = PartyDistributionType.findById(this._partyDistributionTypeId);
        if (partyDistributionType == null) {
            return;
        }
        final Party party = activeChar.getParty();
        if (party == null || !party.isLeader(activeChar) || partyDistributionType == party.getDistributionType()) {
            return;
        }
        party.requestLootChange(partyDistributionType);
    }
}
