// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminSummon implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public String[] getAdminCommandList() {
        return AdminSummon.ADMIN_COMMANDS;
    }
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        int count = 1;
        final String[] data = command.split(" ");
        int id;
        try {
            id = Integer.parseInt(data[1]);
            if (data.length > 2) {
                count = Integer.parseInt(data[2]);
            }
        }
        catch (NumberFormatException nfe) {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect format for command 'summon'");
            return false;
        }
        String subCommand;
        if (id < 1000000) {
            subCommand = "admin_create_item";
        }
        else {
            subCommand = "admin_spawn_once";
            BuilderUtil.sendSysMessage(activeChar, "This is only a temporary spawn.  The mob(s) will NOT respawn.");
            id -= 1000000;
        }
        if (id > 0 && count > 0) {
            AdminCommandHandler.getInstance().useAdminCommand(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, subCommand, id, count), true);
        }
        return true;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_summon" };
    }
}
