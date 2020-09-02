// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.network.serverpackets.ExInzoneWaiting;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class InstanceZone implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public int[] getUserCommandList() {
        return InstanceZone.COMMAND_IDS;
    }
    
    public boolean useUserCommand(final int id, final Player player) {
        if (id != InstanceZone.COMMAND_IDS[0]) {
            return false;
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExInzoneWaiting(player, false) });
        return true;
    }
    
    static {
        COMMAND_IDS = new int[] { 90 };
    }
}
