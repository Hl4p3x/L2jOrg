// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminManor implements IAdminCommandHandler
{
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final CastleManorManager manor = CastleManorManager.getInstance();
        final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
        msg.setFile(activeChar, "data/html/admin/manor.htm");
        msg.replace("%status%", manor.getCurrentModeName());
        msg.replace("%change%", manor.getNextModeChange());
        final StringBuilder sb = new StringBuilder(3400);
        for (final Castle c : CastleManager.getInstance().getCastles()) {
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, c.getName()));
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GameUtils.formatAdena(manor.getManorCost(c.getId(), false))));
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GameUtils.formatAdena(manor.getManorCost(c.getId(), true))));
            sb.append("<tr><td><font color=808080>--------------------------</font></td><td><font color=808080>--------------------------</font></td></tr>");
        }
        msg.replace("%castleInfo%", sb.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg });
        sb.setLength(0);
        return true;
    }
    
    public String[] getAdminCommandList() {
        return new String[] { "admin_manor" };
    }
}
