// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class Dismount implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public synchronized boolean useUserCommand(final int id, final Player player) {
        if (id != Dismount.COMMAND_IDS[0]) {
            return false;
        }
        if (player.isRentedPet()) {
            player.stopRentPet();
        }
        else if (player.isMounted()) {
            player.dismount();
        }
        return true;
    }
    
    public int[] getUserCommandList() {
        return Dismount.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 62 };
    }
}
