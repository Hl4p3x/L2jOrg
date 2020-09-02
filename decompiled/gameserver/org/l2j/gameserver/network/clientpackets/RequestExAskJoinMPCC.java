// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExAskJoinMPCC;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;

public final class RequestExAskJoinMPCC extends ClientPacket
{
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
        final Player requestor = ((GameClient)this.client).getPlayer();
        if (requestor == null) {
            return;
        }
        final Player player = World.getInstance().findPlayer(this._name);
        if (player == null) {
            return;
        }
        if (requestor.isInParty() && player.isInParty() && requestor.getParty().equals(player.getParty())) {
            return;
        }
        if (requestor.isInParty()) {
            final Party activeParty = requestor.getParty();
            if (activeParty.getLeader().equals(requestor)) {
                if (activeParty.isInCommandChannel() && activeParty.getCommandChannel().getLeader().equals(requestor)) {
                    if (player.isInParty()) {
                        if (player.getParty().isInCommandChannel()) {
                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL);
                            sm.addString(player.getName());
                            requestor.sendPacket(sm);
                        }
                        else {
                            this.askJoinMPCC(requestor, player);
                        }
                    }
                    else {
                        requestor.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                    }
                }
                else if (activeParty.isInCommandChannel() && !activeParty.getCommandChannel().getLeader().equals(requestor)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
                    requestor.sendPacket(sm);
                }
                else if (player.isInParty()) {
                    if (player.getParty().isInCommandChannel()) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL);
                        sm.addString(player.getName());
                        requestor.sendPacket(sm);
                    }
                    else {
                        this.askJoinMPCC(requestor, player);
                    }
                }
                else {
                    requestor.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
                }
            }
            else {
                requestor.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
            }
        }
    }
    
    private void askJoinMPCC(final Player requestor, final Player target) {
        boolean hasRight = false;
        if (requestor.isClanLeader() && requestor.getClan().getLevel() >= 5) {
            hasRight = true;
        }
        else if (requestor.getInventory().getItemByItemId(8871) != null) {
            hasRight = true;
        }
        else if (requestor.getPledgeClass() >= 5 && requestor.getKnownSkill(391) != null) {
            hasRight = true;
        }
        if (!hasRight) {
            requestor.sendPacket(SystemMessageId.COMMAND_CHANNELS_CAN_ONLY_BE_FORMED_BY_A_PARTY_LEADER_WHO_IS_ALSO_THE_LEADER_OF_A_LEVEL_5_CLAN);
            return;
        }
        final Player targetLeader = target.getParty().getLeader();
        if (!targetLeader.isProcessingRequest()) {
            requestor.onTransactionRequest(targetLeader);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_INVITING_YOU_TO_A_COMMAND_CHANNEL_DO_YOU_ACCEPT);
            sm.addString(requestor.getName());
            targetLeader.sendPacket(sm);
            targetLeader.sendPacket(new ExAskJoinMPCC(requestor.getName()));
            requestor.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, targetLeader.getName()));
        }
        else {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
            sm.addString(targetLeader.getName());
            requestor.sendPacket(sm);
        }
    }
}
