// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Iterator;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import org.l2j.gameserver.handler.IWriteBoardHandler;

public class ClanBoard implements IWriteBoardHandler
{
    private static final String[] COMMANDS;
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player player) {
        if (tokens.hasMoreTokens()) {
            this.parseClanAction(tokens, player);
        }
        else if (Util.falseIfNullOrElse((Object)player.getClan(), c -> c.getLevel() >= 2)) {
            this.clanHome(player);
        }
        else {
            this.clanList(player, 1);
        }
        return true;
    }
    
    protected void parseClanAction(final StringTokenizer tokens, final Player player) {
        final String nextToken = tokens.nextToken();
        switch (nextToken) {
            case "list": {
                this.clanList(player, Util.parseNextInt(tokens, 1));
                break;
            }
            case "home": {
                this.clanHome(player, Util.parseNextInt(tokens, player.getClanId()));
                break;
            }
            case "notice": {
                this.clanNotice(player, tokens);
                break;
            }
        }
    }
    
    public String[] getCommunityBoardCommands() {
        return ClanBoard.COMMANDS;
    }
    
    private void clanNotice(final Player player, final StringTokenizer tokens) {
        if (tokens.hasMoreTokens()) {
            Util.doIfNonNull((Object)player.getClan(), clan -> clan.setNoticeEnabled("enable".equalsIgnoreCase(tokens.nextToken())));
        }
        this.clanNotice(player);
    }
    
    private void clanNotice(final Player player) {
        StringBuilder html;
        Util.doIfNonNull((Object)player.getClan(), clan -> {
            if (clan.getLevel() < 2) {
                player.sendPacket(SystemMessageId.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER);
            }
            else {
                html = new StringBuilder(2048);
                html.append("<html><body><br><br><table border=0 width=610><tr><td width=10></td><td width=600 align=left><a action=\"bypass _bbshome\">HOME</a> &gt; <a action=\"bypass _bbsclan list\"> CLAN COMMUNITY </a>  &gt; <a action=\"bypass _bbsclan home ");
                html.append(clan.getId());
                html.append("\"> &amp;$802; </a></td></tr></table>");
                if (player.isClanLeader()) {
                    html.append("<br><br><center><table width=610 border=0 cellspacing=0 cellpadding=0><tr><td fixwidth=610><font color=\"AAAAAA\">The Clan Notice function allows the clan leader to send messages through a pop-up window to clan members at login.</font> </td></tr><tr><td height=20></td></tr>");
                    if (player.getClan().isNoticeEnabled()) {
                        html.append("<tr><td fixwidth=610> Clan Notice Function:&nbsp;&nbsp;&nbsp;on&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;<a action=\"bypass _bbsclan notice disable\">off</a>");
                    }
                    else {
                        html.append("<tr><td fixwidth=610> Clan Notice Function:&nbsp;&nbsp;&nbsp;<a action=\"bypass _bbsclan notice enable\">on</a>&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;off");
                    }
                    html.append("</td></tr></table><img src=\"L2UI.Squaregray\" width=\"610\" height=\"1\"><br> <br><table width=610 border=0 cellspacing=2 cellpadding=0><tr><td>Edit Notice: </td></tr><tr><td height=5></td></tr><tr><td><MultiEdit var =\"Content\" width=610 height=100></td></tr></table><br><table width=610 border=0 cellspacing=0 cellpadding=0><tr><td height=5></td></tr><tr><td align=center FIXWIDTH=65><button value=\"&$140;\" action=\"Write _bbsclan Set _ Content Content Content\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td><td align=center FIXWIDTH=45></td><td align=center FIXWIDTH=500></td></tr></table></center></body></html>");
                    GameUtils.sendCBHtml(player, html.toString(), player.getClan().getNotice());
                }
                else {
                    html.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\"><center><table border=0 cellspacing=0 cellpadding=0><tr><td>You are not your clan's leader, and therefore cannot change the clan notice</td></tr></table>");
                    if (player.getClan().isNoticeEnabled()) {
                        html.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getClan().getNotice()));
                    }
                    html.append("</center></body></html>");
                    CommunityBoardHandler.separateAndSend(html.toString(), player);
                }
            }
        });
    }
    
    private void clanList(final Player activeChar, int index) {
        if (index < 1) {
            index = 1;
        }
        final StringBuilder html = new StringBuilder(2048);
        html.append("<html><body><br><br><center><br1><br1><table border=0 cellspacing=0 cellpadding=0><tr><td FIXWIDTH=15>&nbsp;</td><td width=610 height=30 align=left><a action=\"bypass _bbsclan list\"> CLAN COMMUNITY </a></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610 bgcolor=434343><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixWIDTH=600><a action=\"bypass _bbsclan home ");
        html.append((activeChar.getClan() != null) ? activeChar.getClan().getId() : 0);
        html.append("\">[GO TO MY CLAN]</a>&nbsp;&nbsp;</td><td fixWIDTH=5></td></tr><tr><td height=10></td></tr></table><br><table border=0 cellspacing=0 cellpadding=2 bgcolor=5A5A5A width=610><tr><td FIXWIDTH=5></td><td FIXWIDTH=200 align=center>CLAN NAME</td><td FIXWIDTH=200 align=center>CLAN LEADER</td><td FIXWIDTH=100 align=center>CLAN LEVEL</td><td FIXWIDTH=100 align=center>CLAN MEMBERS</td><td FIXWIDTH=5></td></tr></table><img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");
        int i = 0;
        for (final Clan cl : ClanTable.getInstance().getClans()) {
            if (i > (index + 1) * 7) {
                break;
            }
            if (i++ < (index - 1) * 7) {
                continue;
            }
            html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\"><table border=0 cellspacing=0 cellpadding=0 width=610><tr> <td FIXWIDTH=5></td><td FIXWIDTH=200 align=center><a action=\"bypass _bbsclan home ");
            html.append(cl.getId());
            html.append("\">");
            html.append(cl.getName());
            html.append("</a></td><td FIXWIDTH=200 align=center>");
            html.append(cl.getLeaderName());
            html.append("</td><td FIXWIDTH=100 align=center>");
            html.append(cl.getLevel());
            html.append("</td><td FIXWIDTH=100 align=center>");
            html.append(cl.getMembersCount());
            html.append("</td><td FIXWIDTH=5></td></tr><tr><td height=5></td></tr></table><img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\"><img src=\"L2UI.SquareGray\" width=\"610\" height=\"1\">");
        }
        html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"2\"><table cellpadding=0 cellspacing=2 border=0><tr>");
        if (index == 1) {
            html.append("<td><button action=\"\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
        }
        else {
            html.append("<td><button action=\"_bbsclan list ");
            html.append(index - 1);
            html.append("\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
        }
        i = 0;
        int nbp = ClanTable.getInstance().getClanCount() / 8;
        if (nbp * 8 != ClanTable.getInstance().getClanCount()) {
            ++nbp;
        }
        for (i = 1; i <= nbp; ++i) {
            if (i == index) {
                html.append("<td> ");
                html.append(i);
                html.append(" </td>");
            }
            else {
                html.append("<td><a action=\"bypass _bbsclan list ");
                html.append(i);
                html.append("\"> ");
                html.append(i);
                html.append(" </a></td>");
            }
        }
        if (index == nbp) {
            html.append("<td><button action=\"\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
        }
        else {
            html.append("<td><button action=\"bypass _bbsclan list ");
            html.append(index + 1);
            html.append("\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
        }
        html.append("</tr></table><table border=0 cellspacing=0 cellpadding=0><tr><td width=610><img src=\"sek.cbui141\" width=\"610\" height=\"1\"></td></tr></table><table border=0><tr><td><combobox width=65 var=keyword list=\"Name;Ruler\"></td><td><edit var = \"Search\" width=130 height=11 length=\"16\"></td><td><button value=\"&$420;\" action=\"Write 5 -1 0 Search keyword keyword\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td> </tr></table><br><br></center></body></html>");
        CommunityBoardHandler.separateAndSend(html.toString(), activeChar);
    }
    
    private void clanHome(final Player activeChar) {
        this.clanHome(activeChar, activeChar.getClan().getId());
    }
    
    private void clanHome(final Player activeChar, final int clanId) {
        final Clan cl = ClanTable.getInstance().getClan(clanId);
        if (cl != null) {
            if (cl.getLevel() < 2) {
                activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER);
            }
            else {
                final String html = Arrays.asList("<html><body><center><br><br><br1><br1><table border=0 cellspacing=0 cellpadding=0><tr><td FIXWIDTH=15>&nbsp;</td><td width=610 height=30 align=left><a action=\"bypass _bbshome\">HOME</a> &gt; <a action=\"bypass _bbsclan list\"> CLAN COMMUNITY </a>  &gt; <a action=\"bypass _bbsclan home ", String.valueOf(clanId), "\"> &amp;$802; </a></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610 bgcolor=434343><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixwidth=600><a action=\"bypass _bbsclan home ", String.valueOf(clanId), ";announce\">[CLAN ANNOUNCEMENT]</a> <a action=\"bypass _bbsclan home ", String.valueOf(clanId), "\">[CLAN BULLETIN BOARD]</a><a action=\"bypass _bbsclan home ", String.valueOf(clanId), ";cmail\">[CLAN MAIL]</a>&nbsp;&nbsp;<a action=\"bypass _bbsclan notice\">[CLAN NOTICE]</a>&nbsp;&nbsp;</td><td fixWIDTH=5></td></tr><tr><td height=10></td></tr></table><table border=0 cellspacing=0 cellpadding=0 width=610><tr><td height=10></td></tr><tr><td fixWIDTH=5></td><td fixwidth=290 valign=top></td><td fixWIDTH=5></td><td fixWIDTH=5 align=center valign=top><img src=\"l2ui.squaregray\" width=2  height=128></td><td fixWIDTH=5></td><td fixwidth=295><table border=0 cellspacing=0 cellpadding=0 width=295><tr><td fixWIDTH=100 align=left>CLAN NAME</td><td fixWIDTH=195 align=left>", cl.getName(), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN LEVEL</td><td fixWIDTH=195 align=left height=16>", String.valueOf(cl.getLevel()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN MEMBERS</td><td fixWIDTH=195 align=left height=16>", String.valueOf(cl.getMembersCount()), "</td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>CLAN LEADER</td><td fixWIDTH=195 align=left height=16>", cl.getLeaderName(), "</td></tr><tr><td height=7></td></tr><tr><td height=7></td></tr><tr><td fixWIDTH=100 align=left>ALLIANCE</td><td fixWIDTH=195 align=left height=16>", (cl.getAllyName() != null) ? cl.getAllyName() : "", "</td></tr></table></td><td fixWIDTH=5></td></tr><tr><td height=10></td></tr></table><img src=\"L2UI.squareblank\" width=\"1\" height=\"5\"><img src=\"L2UI.squaregray\" width=\"610\" height=\"1\"><br></center><br> <br></body></html>").stream().collect((Collector<? super Object, ?, String>)Collectors.joining());
                CommunityBoardHandler.separateAndSend(html, activeChar);
            }
        }
    }
    
    public boolean writeCommunityBoardCommand(final Player activeChar, final String arg1, final String arg2, final String arg3, final String arg4, final String arg5) {
        final Clan clan = activeChar.getClan();
        if (clan != null && activeChar.isClanLeader()) {
            clan.setNotice(arg3);
        }
        return true;
    }
    
    static {
        COMMANDS = new String[] { "_bbsclan" };
    }
}
