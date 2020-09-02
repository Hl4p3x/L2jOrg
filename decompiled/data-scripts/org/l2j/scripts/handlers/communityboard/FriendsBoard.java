// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import org.l2j.gameserver.handler.IParseBoardHandler;

public class FriendsBoard implements IParseBoardHandler
{
    private static final String[] COMMANDS;
    
    public String[] getCommunityBoardCommands() {
        return FriendsBoard.COMMANDS;
    }
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player activeChar) {
        if (command.equals("_friendlist")) {
            CommunityBoardHandler.getInstance().addBypass(activeChar, "Friends List", command);
            final String html = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/friends_list.html");
            CommunityBoardHandler.separateAndSend(html, activeChar);
        }
        else if (command.equals("_friendblocklist")) {
            CommunityBoardHandler.getInstance().addBypass(activeChar, "Ignore list", command);
            final String html = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/friends_block_list.html");
            CommunityBoardHandler.separateAndSend(html, activeChar);
        }
        return true;
    }
    
    static {
        COMMANDS = new String[] { "_friendlist", "_friendblocklist" };
    }
}
