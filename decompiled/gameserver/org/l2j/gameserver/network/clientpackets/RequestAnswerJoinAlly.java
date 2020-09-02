// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class RequestAnswerJoinAlly extends ClientPacket
{
    private int _response;
    
    public void readImpl() {
        this._response = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Player requestor = activeChar.getRequest().getPartner();
        if (requestor == null) {
            return;
        }
        if (this._response == 0) {
            activeChar.sendPacket(SystemMessageId.NO_RESPONSE_YOUR_ENTRANCE_TO_THE_ALLIANCE_HAS_BEEN_CANCELLED);
            requestor.sendPacket(SystemMessageId.NO_RESPONSE_INVITATION_TO_JOIN_AN_ALLIANCE_HAS_BEEN_CANCELLED);
        }
        else {
            if (!(requestor.getRequest().getRequestPacket() instanceof RequestJoinAlly)) {
                return;
            }
            final Clan clan = requestor.getClan();
            if (clan.checkAllyJoinCondition(requestor, activeChar)) {
                requestor.sendPacket(SystemMessageId.THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST);
                activeChar.sendPacket(SystemMessageId.YOU_HAVE_ACCEPTED_THE_ALLIANCE);
                activeChar.getClan().setAllyId(clan.getAllyId());
                activeChar.getClan().setAllyName(clan.getAllyName());
                activeChar.getClan().setAllyPenaltyExpiryTime(0L, 0);
                activeChar.getClan().changeAllyCrest(clan.getAllyCrestId(), true);
                activeChar.getClan().updateClanInDB();
            }
        }
        activeChar.getRequest().onRequestResponse();
    }
}
