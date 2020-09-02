// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminDelete implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_delete")) {
            this.handleDelete(activeChar);
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminDelete.ADMIN_COMMANDS;
    }
    
    private void handleDelete(final Player activeChar) {
        final WorldObject obj = activeChar.getTarget();
        if (obj instanceof Npc) {
            final Npc target = (Npc)obj;
            target.deleteMe();
            final Spawn spawn = target.getSpawn();
            if (spawn != null) {
                spawn.stopRespawn();
                if (DBSpawnManager.getInstance().isDefined(spawn.getId())) {
                    DBSpawnManager.getInstance().deleteSpawn(spawn, true);
                }
                else {
                    SpawnTable.getInstance().deleteSpawn(spawn, true);
                }
            }
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, target.getName(), target.getObjectId()));
        }
        else {
            BuilderUtil.sendSysMessage(activeChar, "Incorrect target.");
        }
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_delete" };
    }
}
