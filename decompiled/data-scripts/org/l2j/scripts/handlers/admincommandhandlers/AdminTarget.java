// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminTarget implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_target")) {
            this.handleTarget(command, activeChar);
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminTarget.ADMIN_COMMANDS;
    }
    
    private void handleTarget(final String command, final Player activeChar) {
        try {
            final String targetName = command.substring(13);
            final Player player = World.getInstance().findPlayer(targetName);
            if (player != null) {
                player.onAction(activeChar);
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, targetName));
            }
        }
        catch (IndexOutOfBoundsException e) {
            BuilderUtil.sendSysMessage(activeChar, "Please specify correct name.");
        }
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_target" };
    }
}
