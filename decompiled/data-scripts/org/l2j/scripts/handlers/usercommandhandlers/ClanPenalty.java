// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class ClanPenalty implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (id != ClanPenalty.COMMAND_IDS[0]) {
            return false;
        }
        boolean penalty = false;
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final StringBuilder htmlContent = new StringBuilder(500);
        htmlContent.append("<html><body><center><table width=270 border=0 bgcolor=111111><tr><td width=170>Penalty</td><td width=100 align=center>Expiration Date</td></tr></table><table width=270 border=0><tr>");
        if (player.getClanJoinExpiryTime() > System.currentTimeMillis()) {
            htmlContent.append("<td width=170>Unable to join a clan.</td><td width=100 align=center>");
            htmlContent.append(format.format(player.getClanJoinExpiryTime()));
            htmlContent.append("</td>");
            penalty = true;
        }
        if (player.getClanCreateExpiryTime() > System.currentTimeMillis()) {
            htmlContent.append("<td width=170>Unable to create a clan.</td><td width=100 align=center>");
            htmlContent.append(format.format(player.getClanCreateExpiryTime()));
            htmlContent.append("</td>");
            penalty = true;
        }
        if (player.getClan() != null && player.getClan().getCharPenaltyExpiryTime() > System.currentTimeMillis()) {
            htmlContent.append("<td width=170>Unable to invite a clan member.</td><td width=100 align=center>");
            htmlContent.append(format.format(player.getClan().getCharPenaltyExpiryTime()));
            htmlContent.append("</td>");
            penalty = true;
        }
        if (!penalty) {
            htmlContent.append("<td width=170>No penalty is imposed.</td><td width=100 align=center></td>");
        }
        htmlContent.append("</tr></table><img src=\"L2UI.SquareWhite\" width=270 height=1></center></body></html>");
        final NpcHtmlMessage penaltyHtml = new NpcHtmlMessage();
        penaltyHtml.setHtml(htmlContent.toString());
        player.sendPacket(new ServerPacket[] { (ServerPacket)penaltyHtml });
        return true;
    }
    
    public int[] getUserCommandList() {
        return ClanPenalty.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 100 };
    }
}
