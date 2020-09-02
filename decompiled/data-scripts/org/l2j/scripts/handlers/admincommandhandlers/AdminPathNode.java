// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminPathNode implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_path_find")) {
            if (activeChar.getTarget() != null) {
                final List<Location> path = (List<Location>)GeoEngine.getInstance().findPath(activeChar.getX(), activeChar.getY(), (int)(short)activeChar.getZ(), activeChar.getTarget().getX(), activeChar.getTarget().getY(), (int)(short)activeChar.getTarget().getZ(), activeChar.getInstanceWorld());
                if (path == null) {
                    BuilderUtil.sendSysMessage(activeChar, "No route found or pathfinding disabled.");
                }
                else {
                    for (final Location point : path) {
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, point.getX(), point.getY(), point.getZ()));
                    }
                }
            }
            else {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
            return true;
        }
        return false;
    }
    
    public String[] getAdminCommandList() {
        return AdminPathNode.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_path_find" };
    }
}
