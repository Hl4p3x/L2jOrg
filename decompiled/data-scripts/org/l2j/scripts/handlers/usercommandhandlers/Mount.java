// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class Mount implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public synchronized boolean useUserCommand(final int id, final Player player) {
        return id == Mount.COMMAND_IDS[0] && player.mountPlayer((Summon)player.getPet());
    }
    
    public int[] getUserCommandList() {
        return Mount.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 61 };
    }
}
