// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import org.l2j.gameserver.handler.IWriteBoardHandler;

public class MailBoard implements IWriteBoardHandler
{
    private static final String[] COMMANDS;
    
    public String[] getCommunityBoardCommands() {
        return MailBoard.COMMANDS;
    }
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player activeChar) {
        CommunityBoardHandler.getInstance().addBypass(activeChar, "Mail Command", command);
        final String html = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/mail.html");
        CommunityBoardHandler.separateAndSend(html, activeChar);
        return true;
    }
    
    public boolean writeCommunityBoardCommand(final Player activeChar, final String arg1, final String arg2, final String arg3, final String arg4, final String arg5) {
        return false;
    }
    
    static {
        COMMANDS = new String[] { "_maillist" };
    }
}
