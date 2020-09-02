// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.serverpackets.ExDuelAskStart;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;

public final class RequestDuelStart extends ClientPacket
{
    private String _player;
    private int _partyDuel;
    
    public void readImpl() {
        this._player = this.readString();
        this._partyDuel = this.readInt();
    }
    
    private void scheduleDeny(final Player player, final String name) {
        if (player != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
            sm.addString(name);
            player.sendPacket(sm);
            player.onTransactionResponse();
        }
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Player targetChar = World.getInstance().findPlayer(this._player);
        if (targetChar == null) {
            activeChar.sendPacket(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
            return;
        }
        if (activeChar == targetChar) {
            activeChar.sendPacket(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
            return;
        }
        if (!activeChar.canDuel()) {
            activeChar.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
            return;
        }
        if (!targetChar.canDuel()) {
            activeChar.sendPacket(targetChar.getNoDuelReason());
            return;
        }
        if (!MathUtil.isInsideRadius2D(activeChar, targetChar, 250)) {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_TOO_FAR_AWAY_TO_RECEIVE_A_DUEL_CHALLENGE);
            msg.addString(targetChar.getName());
            activeChar.sendPacket(msg);
            return;
        }
        if (this._partyDuel == 1) {
            final Party party = activeChar.getParty();
            if (party == null || !party.isLeader(activeChar)) {
                activeChar.sendMessage("You have to be the leader of a party in order to request a party duel.");
                return;
            }
            if (!targetChar.isInParty()) {
                activeChar.sendPacket(SystemMessageId.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
                return;
            }
            if (activeChar.getParty().containsPlayer(targetChar)) {
                activeChar.sendMessage("This player is a member of your own party.");
                return;
            }
            for (final Player temp : activeChar.getParty().getMembers()) {
                if (!temp.canDuel()) {
                    activeChar.sendMessage("Not all the members of your party are ready for a duel.");
                    return;
                }
            }
            Player partyLeader = null;
            for (final Player temp2 : targetChar.getParty().getMembers()) {
                if (partyLeader == null) {
                    partyLeader = temp2;
                }
                if (!temp2.canDuel()) {
                    activeChar.sendPacket(SystemMessageId.THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL);
                    return;
                }
            }
            if (partyLeader != null) {
                if (!partyLeader.isProcessingRequest()) {
                    activeChar.onTransactionRequest(partyLeader);
                    partyLeader.sendPacket(new ExDuelAskStart(activeChar.getName(), this._partyDuel));
                    SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL);
                    msg2.addString(partyLeader.getName());
                    activeChar.sendPacket(msg2);
                    msg2 = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL);
                    msg2.addString(activeChar.getName());
                    targetChar.sendPacket(msg2);
                }
                else {
                    final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
                    msg2.addString(partyLeader.getName());
                    activeChar.sendPacket(msg2);
                }
            }
        }
        else if (!targetChar.isProcessingRequest()) {
            activeChar.onTransactionRequest(targetChar);
            targetChar.sendPacket(new ExDuelAskStart(activeChar.getName(), this._partyDuel));
            SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_CHALLENGED_TO_A_DUEL);
            msg.addString(targetChar.getName());
            activeChar.sendPacket(msg);
            msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_CHALLENGED_YOU_TO_A_DUEL);
            msg.addString(activeChar.getName());
            targetChar.sendPacket(msg);
        }
        else {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
            msg.addString(targetChar.getName());
            activeChar.sendPacket(msg);
        }
    }
}
