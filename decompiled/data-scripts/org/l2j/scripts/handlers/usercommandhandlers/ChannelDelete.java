// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class ChannelDelete implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (id != ChannelDelete.COMMAND_IDS[0]) {
            return false;
        }
        if (player.isInParty() && player.getParty().isLeader(player) && player.getParty().isInCommandChannel() && player.getParty().getCommandChannel().getLeader().equals((Object)player)) {
            final CommandChannel channel = player.getParty().getCommandChannel();
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED);
            channel.broadcastPacket((ServerPacket)sm);
            channel.disbandChannel();
            return true;
        }
        return false;
    }
    
    public int[] getUserCommandList() {
        return ChannelDelete.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 93 };
    }
}
