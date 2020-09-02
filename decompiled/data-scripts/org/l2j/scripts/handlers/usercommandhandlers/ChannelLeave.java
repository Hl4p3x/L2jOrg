// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class ChannelLeave implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (id != ChannelLeave.COMMAND_IDS[0]) {
            return false;
        }
        if (!player.isInParty() || !player.getParty().isLeader(player)) {
            player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_LEAVE_A_COMMAND_CHANNEL);
            return false;
        }
        if (player.getParty().isInCommandChannel()) {
            final CommandChannel channel = player.getParty().getCommandChannel();
            final Party party = player.getParty();
            channel.removeParty(party);
            party.getLeader().sendPacket(SystemMessageId.YOU_HAVE_QUIT_THE_COMMAND_CHANNEL);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_LEFT_THE_COMMAND_CHANNEL);
            sm.addPcName(party.getLeader());
            channel.broadcastPacket((ServerPacket)sm);
            return true;
        }
        return false;
    }
    
    public int[] getUserCommandList() {
        return ChannelLeave.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 96 };
    }
}
