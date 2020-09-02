// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Calendar;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.instancemanager.GraciaSeedsManager;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminGraciaSeeds implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        String val = "";
        if (st.countTokens() >= 1) {
            val = st.nextToken();
        }
        if (actualCommand.equalsIgnoreCase("admin_kill_tiat")) {
            GraciaSeedsManager.getInstance().increaseSoDTiatKilled();
        }
        else if (actualCommand.equalsIgnoreCase("admin_set_sodstate")) {
            GraciaSeedsManager.getInstance().setSoDState(Integer.parseInt(val), true);
        }
        this.showMenu(activeChar);
        return true;
    }
    
    private void showMenu(final Player activeChar) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/graciaseeds.htm");
        html.replace("%sodstate%", String.valueOf(GraciaSeedsManager.getInstance().getSoDState()));
        html.replace("%sodtiatkill%", String.valueOf(GraciaSeedsManager.getInstance().getSoDTiatKilled()));
        if (GraciaSeedsManager.getInstance().getSoDTimeForNextStateChange() > 0L) {
            final Calendar nextChangeDate = Calendar.getInstance();
            nextChangeDate.setTimeInMillis(System.currentTimeMillis() + GraciaSeedsManager.getInstance().getSoDTimeForNextStateChange());
            html.replace("%sodtime%", nextChangeDate.getTime().toString());
        }
        else {
            html.replace("%sodtime%", "-1");
        }
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    public String[] getAdminCommandList() {
        return AdminGraciaSeeds.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_gracia_seeds", "admin_kill_tiat", "admin_set_sodstate" };
    }
}
