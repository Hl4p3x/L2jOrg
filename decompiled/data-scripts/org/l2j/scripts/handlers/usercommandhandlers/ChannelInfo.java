// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class ChannelInfo implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (id != ChannelInfo.COMMAND_IDS[0]) {
            return false;
        }
        if (player.getParty() == null || player.getParty().getCommandChannel() == null) {
            return false;
        }
        final CommandChannel channel = player.getParty().getCommandChannel();
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExMultiPartyCommandChannelInfo(channel) });
        return true;
    }
    
    public int[] getUserCommandList() {
        return ChannelInfo.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 97 };
    }
}
