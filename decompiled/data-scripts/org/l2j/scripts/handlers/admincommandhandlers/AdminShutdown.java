// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.Shutdown;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminShutdown implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_server_shutdown")) {
            try {
                final String val = command.substring(22);
                if (Util.isInteger(val)) {
                    this.serverShutdown(activeChar, Integer.valueOf(val), false);
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //server_shutdown <seconds>");
                    this.sendHtmlForm(activeChar);
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                this.sendHtmlForm(activeChar);
            }
        }
        else if (command.startsWith("admin_server_restart")) {
            try {
                final String val = command.substring(21);
                if (Util.isInteger(val)) {
                    this.serverShutdown(activeChar, Integer.parseInt(val), true);
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //server_restart <seconds>");
                    this.sendHtmlForm(activeChar);
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                this.sendHtmlForm(activeChar);
            }
        }
        else if (command.startsWith("admin_server_abort")) {
            this.serverAbort(activeChar);
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminShutdown.ADMIN_COMMANDS;
    }
    
    private void sendHtmlForm(final Player activeChar) {
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final int t = WorldTimeController.getInstance().getGameTime();
        final int h = t / 60;
        final int m = t % 60;
        final SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        final Calendar cal = Calendar.getInstance();
        cal.set(11, h);
        cal.set(12, m);
        adminReply.setFile(activeChar, "data/html/admin/shutdown.htm");
        adminReply.replace("%count%", String.valueOf(World.getInstance().getPlayers().size()));
        adminReply.replace("%used%", String.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        adminReply.replace("%time%", format.format(cal.getTime()));
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void serverShutdown(final Player activeChar, final int seconds, final boolean restart) {
        Shutdown.getInstance().startShutdown(activeChar, seconds, restart);
    }
    
    private void serverAbort(final Player activeChar) {
        Shutdown.getInstance().abort(activeChar);
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_server_shutdown", "admin_server_restart", "admin_server_abort" };
    }
}
