// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.JoinParty;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.PartyRequest;
import org.l2j.gameserver.network.GameClient;

public final class RequestAnswerJoinParty extends ClientPacket
{
    private int _response;
    
    public void readImpl() {
        this._response = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final PartyRequest request = player.getRequest(PartyRequest.class);
        if (request == null || request.isProcessing() || !player.removeRequest(request.getClass())) {
            return;
        }
        request.setProcessing(true);
        final Player requestor = request.getPlayer();
        if (requestor == null) {
            return;
        }
        final Party party = request.getParty();
        final Party requestorParty = requestor.getParty();
        if (requestorParty != null && requestorParty != party) {
            return;
        }
        requestor.sendPacket(new JoinParty(this._response));
        if (this._response == 1) {
            if (party.getMemberCount() >= Config.ALT_PARTY_MAX_MEMBERS) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_PARTY_IS_FULL);
                player.sendPacket(sm);
                requestor.sendPacket(sm);
                return;
            }
            if (requestorParty == null) {
                requestor.setParty(party);
            }
            player.joinParty(party);
            final MatchingRoom requestorRoom = requestor.getMatchingRoom();
            if (requestorRoom != null) {
                requestorRoom.addMember(player);
            }
        }
        else if (this._response == -1) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_PARTY_REQUESTS_AND_CANNOT_RECEIVE_A_PARTY_REQUEST);
            sm.addPcName(player);
            requestor.sendPacket(sm);
            if (party.getMemberCount() == 1) {
                party.removePartyMember(requestor, Party.MessageType.NONE);
            }
        }
        else if (party.getMemberCount() == 1) {
            party.removePartyMember(requestor, Party.MessageType.NONE);
        }
        party.setPendingInvitation(false);
        request.setProcessing(false);
    }
}
