// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminHtml implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        final String lowerCase = actualCommand.toLowerCase();
        switch (lowerCase) {
            case "admin_html": {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //html path");
                    return false;
                }
                final String path = st.nextToken();
                showAdminHtml(activeChar, path);
                break;
            }
            case "admin_loadhtml": {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //loadhtml path");
                    return false;
                }
                final String path = st.nextToken();
                showHtml(activeChar, path, true);
                break;
            }
        }
        return true;
    }
    
    static void showAdminHtml(final Player activeChar, final String path) {
        showHtml(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path), false);
    }
    
    private static void showHtml(final Player activeChar, final String path, final boolean reload) {
        String content = null;
        if (!reload) {
            content = HtmCache.getInstance().getHtm(activeChar, path);
        }
        else {
            content = HtmCache.getInstance().loadFile(path);
        }
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        if (content != null) {
            html.setHtml(content);
        }
        else {
            html.setHtml(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, path));
        }
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    public String[] getAdminCommandList() {
        return AdminHtml.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_html", "admin_loadhtml" };
    }
}
