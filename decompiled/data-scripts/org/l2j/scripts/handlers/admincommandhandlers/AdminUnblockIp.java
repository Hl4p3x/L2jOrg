// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminUnblockIp implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_unblockip ")) {
            try {
                final String ipAddress = command.substring(16);
                if (this.unblockIp(ipAddress, activeChar)) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ipAddress));
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //unblockip <ip>");
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminUnblockIp.ADMIN_COMMANDS;
    }
    
    private boolean unblockIp(final String ipAddress, final Player activeChar) {
        AdminUnblockIp.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
        return true;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminUnblockIp.class);
        ADMIN_COMMANDS = new String[] { "admin_unblockip" };
    }
}
