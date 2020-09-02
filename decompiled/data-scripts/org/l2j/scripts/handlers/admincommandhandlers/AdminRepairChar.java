// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.dao.ShortcutDAO;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminRepairChar implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        this.handleRepair(command);
        return true;
    }
    
    private void handleRepair(final String command) {
        final String[] parts = command.split(" ");
        if (parts.length != 2) {
            return;
        }
        final String name = parts[1];
        final int objectId = PlayerNameTable.getInstance().getIdByName(name);
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateToValidLocation(objectId);
        ((ShortcutDAO)DatabaseAccess.getDAO((Class)ShortcutDAO.class)).deleteAll(objectId);
        ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).updateToInventory(objectId);
    }
    
    public String[] getAdminCommandList() {
        return AdminRepairChar.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_restore", "admin_repair" };
    }
}
