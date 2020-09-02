// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.network.GameClient;

public final class RequestPetition extends ClientPacket
{
    private String _content;
    private int _type;
    
    public void readImpl() {
        this._content = this.readString();
        this._type = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._type <= 0 || this._type >= 10) {
            return;
        }
        if (!PetitionManager.getInstance().isPetitioningAllowed()) {
            player.sendMessage("The game client encountered an error and was unable to connect to the petition server.");
            return;
        }
        if (PetitionManager.getInstance().isPlayerPetitionPending(player)) {
            player.sendMessage("You may only submit one petition (active) at a time.");
            return;
        }
        if (PetitionManager.getInstance().getPendingPetitionCount() == Config.MAX_PETITIONS_PENDING) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THE_PETITION_SERVICE_IS_CURRENTLY_UNAVAILABLE_PLEASE_SEND_A_SUPPORT_TICKET_ON_SUPPORT_IF_YOU_BECOME_TRAPPED_OR_UNABLE_TO_MOVE_PLEASE_USE_THE_UNSTUCK_COMMAND);
            return;
        }
        final int totalPetitions = PetitionManager.getInstance().getPlayerTotalPetitionCount(player) + 1;
        if (totalPetitions > Config.MAX_PETITIONS_PER_PLAYER) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM_THAT_YOU_CAN_SUBMIT_IN_ONE_DAY_YOU_CANNOT_SUBMIT_ANY_MORE_PETITIONS);
            sm.addInt(totalPetitions);
            ((GameClient)this.client).sendPacket(sm);
            return;
        }
        if (this._content.length() > 255) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THE_PETITION_CAN_CONTAIN_UP_TO_800_CHARACTERS);
            return;
        }
        final int petitionId = PetitionManager.getInstance().submitPetition(player, this._content, this._type);
        SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PETITION_APPLICATION_HAS_BEEN_ACCEPTED_NRECEIPT_NO_IS_S1);
        sm2.addInt(petitionId);
        ((GameClient)this.client).sendPacket(sm2);
        sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUBMITTED_S1_PETITION_S_NYOU_MAY_SUBMIT_S2_MORE_PETITION_S_TODAY);
        sm2.addInt(totalPetitions);
        sm2.addInt(Config.MAX_PETITIONS_PER_PLAYER - totalPetitions);
        ((GameClient)this.client).sendPacket(sm2);
        sm2 = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S1_PETITIONS_CURRENTLY_ON_THE_WAITING_LIST);
        sm2.addInt(PetitionManager.getInstance().getPendingPetitionCount());
        ((GameClient)this.client).sendPacket(sm2);
    }
}
