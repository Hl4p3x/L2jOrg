// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import org.l2j.gameserver.handler.IParseBoardHandler;

public class HomepageBoard implements IParseBoardHandler
{
    private static final String[] COMMANDS;
    
    public String[] getCommunityBoardCommands() {
        return HomepageBoard.COMMANDS;
    }
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player activeChar) {
        CommunityBoardHandler.separateAndSend(HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/homepage.html"), activeChar);
        return true;
    }
    
    static {
        COMMANDS = new String[] { "_bbslink" };
    }
}
