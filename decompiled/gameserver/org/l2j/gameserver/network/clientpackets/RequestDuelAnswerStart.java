// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public final class RequestDuelAnswerStart extends ClientPacket
{
    private int _partyDuel;
    private int _unk1;
    private int _response;
    
    public void readImpl() {
        this._partyDuel = this.readInt();
        this._unk1 = this.readInt();
        this._response = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Player requestor = player.getActiveRequester();
        if (requestor == null) {
            return;
        }
        if (this._response == 1) {
            SystemMessage msg1 = null;
            SystemMessage msg2 = null;
            if (requestor.isInDuel()) {
                msg1 = SystemMessage.getSystemMessage(SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL);
                msg1.addString(requestor.getName());
                player.sendPacket(msg1);
                return;
            }
            if (player.isInDuel()) {
                player.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
                return;
            }
            if (this._partyDuel == 1) {
                msg1 = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACCEPTED_C1_S_CHALLENGE_TO_A_PARTY_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
                msg1.addString(requestor.getName());
                msg2 = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
                msg2.addString(player.getName());
            }
            else {
                msg1 = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACCEPTED_C1_S_CHALLENGE_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
                msg1.addString(requestor.getName());
                msg2 = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS);
                msg2.addString(player.getName());
            }
            player.sendPacket(msg1);
            requestor.sendPacket(msg2);
            DuelManager.getInstance().addDuel(requestor, player, this._partyDuel);
        }
        else if (this._response == -1) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST);
            sm.addPcName(player);
            requestor.sendPacket(sm);
        }
        else {
            SystemMessage msg3 = null;
            if (this._partyDuel == 1) {
                msg3 = SystemMessage.getSystemMessage(SystemMessageId.THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
            }
            else {
                msg3 = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
                msg3.addPcName(player);
            }
            requestor.sendPacket(msg3);
        }
        player.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}
