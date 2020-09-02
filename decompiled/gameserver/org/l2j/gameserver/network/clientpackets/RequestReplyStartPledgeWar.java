// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.network.GameClient;

public final class RequestReplyStartPledgeWar extends ClientPacket
{
    private int _answer;
    
    public void readImpl() {
        this.readString();
        this._answer = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Player requestor = activeChar.getActiveRequester();
        if (requestor == null) {
            return;
        }
        if (this._answer == 1) {
            final Clan attacked = activeChar.getClan();
            final Clan attacker = requestor.getClan();
            if (attacked != null && attacker != null) {
                final ClanWar clanWar = attacker.getWarWith(attacked.getId());
                if (clanWar.getState() == ClanWarState.BLOOD_DECLARATION) {
                    clanWar.mutualClanWarAccepted(attacker, attacked);
                    clanWar.save();
                }
            }
        }
        else {
            requestor.sendPacket(SystemMessageId.THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED);
        }
        activeChar.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}
