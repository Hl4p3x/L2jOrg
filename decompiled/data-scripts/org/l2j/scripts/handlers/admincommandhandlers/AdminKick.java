// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Iterator;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.world.World;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminKick implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_kick")) {
            final StringTokenizer st = new StringTokenizer(command);
            if (st.countTokens() > 1) {
                st.nextToken();
                final String player = st.nextToken();
                final Player plyr = World.getInstance().findPlayer(player);
                if (plyr != null) {
                    Disconnection.of(plyr).defaultSequence(false);
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, plyr.getName()));
                }
            }
        }
        if (command.startsWith("admin_kick_non_gm")) {
            int counter = 0;
            for (final Player player2 : World.getInstance().getPlayers()) {
                if (!player2.isGM()) {
                    ++counter;
                    Disconnection.of(player2).defaultSequence(false);
                }
            }
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, counter));
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminKick.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_kick", "admin_kick_non_gm" };
    }
}
