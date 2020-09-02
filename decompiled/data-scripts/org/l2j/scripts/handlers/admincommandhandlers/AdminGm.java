// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminGm implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_gm") && activeChar.isGM()) {
            AdminData.getInstance().deleteGm(activeChar);
            activeChar.setAccessLevel(0, true, false);
            BuilderUtil.sendSysMessage(activeChar, "You deactivated your GM access for this session, if you login again you will be GM again, in order to remove your access completely please shift yourself and set your accesslevel to 0.");
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminGm.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_gm" };
    }
}
