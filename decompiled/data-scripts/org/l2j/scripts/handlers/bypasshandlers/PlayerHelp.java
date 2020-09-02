// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class PlayerHelp implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        try {
            if (command.length() < 13) {
                return false;
            }
            final String path = command.substring(12);
            if (path.contains("..")) {
                return false;
            }
            final StringTokenizer st = new StringTokenizer(path);
            final String[] cmd = st.nextToken().split("#");
            NpcHtmlMessage html;
            if (cmd.length > 1) {
                final int itemId = Integer.parseInt(cmd[1]);
                html = new NpcHtmlMessage(0, itemId);
            }
            else {
                html = new NpcHtmlMessage();
            }
            html.setFile(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, cmd[0]));
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        catch (Exception e) {
            PlayerHelp.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)e);
        }
        return true;
    }
    
    public String[] getBypassList() {
        return PlayerHelp.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "player_help" };
    }
}
