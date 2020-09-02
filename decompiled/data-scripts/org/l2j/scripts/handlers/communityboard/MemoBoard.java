// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import org.l2j.gameserver.network.SystemMessageId;
import java.util.Iterator;
import org.l2j.gameserver.data.database.data.CommunityMemo;
import java.util.Collection;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ShowBoard;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.CommunityDAO;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import org.l2j.gameserver.handler.IWriteBoardHandler;

public class MemoBoard implements IWriteBoardHandler
{
    private static final String MEMO_TEMPLATE = "<table border=0 cellspacing=0 cellpadding=5 WIDTH=755>\n<tr><td FIXWIDTH=5></td><td FIXWIDTH=500><a action=\"bypass _bbsmemo read %d\">%s</a></td><td FIXWIDTH=145 align=center></td><td FIXWIDTH=75 align=center>%s</td></tr>\n</table>\n<img src=\"L2UI.Squaregray\" width=\"755\" height=\"1\">\n";
    private static final String[] COMMANDS;
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player player) {
        if (tokens.hasMoreTokens()) {
            this.parseAction(tokens, player);
        }
        else {
            CommunityBoardHandler.separateAndSend(this.home(player), player);
        }
        return true;
    }
    
    protected void parseAction(final StringTokenizer tokens, final Player player) {
        final String nextToken = tokens.nextToken();
        switch (nextToken) {
            case "write": {
                this.writeMemo(player);
                break;
            }
            case "read": {
                this.readMemo(player, Util.parseNextInt(tokens, 0));
                break;
            }
            case "modify": {
                this.modifyMemo(player, Util.parseNextInt(tokens, 0));
                break;
            }
            case "del": {
                this.deleteMemo(player, Util.parseNextInt(tokens, 0));
                break;
            }
        }
    }
    
    private void deleteMemo(final Player player, final int memoId) {
        ((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).deleteMemo(player.getObjectId(), memoId);
        CommunityBoardHandler.separateAndSend(this.home(player), player);
    }
    
    private void modifyMemo(final Player player, final int memoId) {
        final String html;
        final ServerPacket[] array;
        final ShowBoard showBoard;
        final Object o;
        Util.doIfNonNull((Object)((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).findMemo(memoId, player.getObjectId()), memo -> {
            html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/memo_write.html").replace("%id%", String.valueOf(memoId));
            player.sendPacket(new ServerPacket[] { (ServerPacket)new ShowBoard(html, "1001") });
            array = new ServerPacket[] { null };
            new ShowBoard((List)List.of(new String[] { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", memo.getTitle(), "0", memo.getText(), "0", "0", "0", "0" }));
            array[o] = (ServerPacket)showBoard;
            player.sendPacket(array);
        });
    }
    
    private void readMemo(final Player player, final int memoId) {
        final String html;
        Util.doIfNonNull((Object)((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).findMemo(memoId, player.getObjectId()), memo -> {
            html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/memo_detail.html").replace("%title%", memo.getTitle()).replace("%player%", player.getName()).replace("%datetime%", Util.formatDateTime(memo.getDate())).replace("%text%", memo.getText()).replace("%id%", String.valueOf(memo.getId()));
            CommunityBoardHandler.separateAndSend(html, player);
        });
    }
    
    private void writeMemo(final Player player) {
        CommunityBoardHandler.separateAndSend(HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/memo_write.html").replace("%id%", "0"), player);
    }
    
    protected String home(final Player player) {
        final String data = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/memo.html");
        final List<CommunityMemo> memos = (List<CommunityMemo>)((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).findMemosBasicInfo(player.getObjectId());
        if (Util.isNullOrEmpty((Collection)memos)) {
            return data.replace("%memo_list%", "");
        }
        return data.replace("%memo_list%", this.memosHtml(memos));
    }
    
    private String memosHtml(final List<CommunityMemo> memos) {
        final StringBuilder builder = new StringBuilder();
        for (final CommunityMemo memo : memos) {
            builder.append(String.format("<table border=0 cellspacing=0 cellpadding=5 WIDTH=755>\n<tr><td FIXWIDTH=5></td><td FIXWIDTH=500><a action=\"bypass _bbsmemo read %d\">%s</a></td><td FIXWIDTH=145 align=center></td><td FIXWIDTH=75 align=center>%s</td></tr>\n</table>\n<img src=\"L2UI.Squaregray\" width=\"755\" height=\"1\">\n", memo.getId(), memo.getTitle(), Util.formatDate(memo.getDate())));
        }
        return builder.append("<br>").toString();
    }
    
    public boolean writeCommunityBoardCommand(final Player player, final String id, final String arg, final String title, final String text, final String arg5) {
        if (title.length() > 80) {
            player.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED);
            return false;
        }
        if (text.length() > 500) {
            player.sendPacket(SystemMessageId.THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED);
            return false;
        }
        final int memoId = Integer.parseInt(id);
        if (memoId == 0) {
            ((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).saveMemo(player.getObjectId(), title, text);
        }
        else {
            ((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).updateMemo(player.getObjectId(), memoId, title, text);
        }
        CommunityBoardHandler.separateAndSend(this.home(player), player);
        return true;
    }
    
    public String[] getCommunityBoardCommands() {
        return MemoBoard.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "_bbsmemo" };
    }
}
