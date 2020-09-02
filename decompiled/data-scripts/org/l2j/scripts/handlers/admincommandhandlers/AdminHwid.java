// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminHwid implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (!GameUtils.isPlayer(activeChar.getTarget()) || activeChar.getTarget().getActingPlayer().getClient() == null || activeChar.getTarget().getActingPlayer().getClient().getHardwareInfo() == null) {
            return true;
        }
        final Player target = activeChar.getTarget().getActingPlayer();
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/charhwinfo.htm"));
        html.replace("%name%", target.getName());
        html.replace("%macAddress%", target.getClient().getHardwareInfo().getMacAddress());
        html.replace("%windowsPlatformId%", target.getClient().getHardwareInfo().getWindowsPlatformId());
        html.replace("%windowsMajorVersion%", target.getClient().getHardwareInfo().getWindowsMajorVersion());
        html.replace("%windowsMinorVersion%", target.getClient().getHardwareInfo().getWindowsMinorVersion());
        html.replace("%windowsBuildNumber%", target.getClient().getHardwareInfo().getWindowsBuildNumber());
        html.replace("%cpuName%", target.getClient().getHardwareInfo().getCpuName());
        html.replace("%cpuSpeed%", target.getClient().getHardwareInfo().getCpuSpeed());
        html.replace("%cpuCoreCount%", target.getClient().getHardwareInfo().getCpuCoreCount());
        html.replace("%vgaName%", target.getClient().getHardwareInfo().getVgaName());
        html.replace("%vgaDriverVersion%", target.getClient().getHardwareInfo().getVgaDriverVersion());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminHwid.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_hwid", "admin_hwinfo" };
    }
}
