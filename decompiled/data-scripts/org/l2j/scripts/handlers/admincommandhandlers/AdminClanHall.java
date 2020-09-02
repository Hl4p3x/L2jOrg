// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Date;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.html.IBypassFormatter;
import org.l2j.gameserver.model.html.formatters.BypassParserFormatter;
import org.l2j.gameserver.model.html.IPageHandler;
import org.l2j.gameserver.model.html.pagehandlers.NextPrevPageHandler;
import java.util.Collection;
import org.l2j.gameserver.model.html.PageBuilder;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import org.l2j.gameserver.model.residences.AbstractResidence;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.util.BypassParser;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminClanHall implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        if (actualCommand.toLowerCase().equals("admin_clanhall")) {
            this.processBypass(activeChar, new BypassParser(command));
        }
        return true;
    }
    
    private void doAction(final Player player, final int clanHallId, final String action, final String actionVal) {
        final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(clanHallId);
        if (clanHall != null) {
            switch (action) {
                case "openCloseDoors": {
                    if (actionVal != null) {
                        clanHall.openCloseDoors(Boolean.parseBoolean(actionVal));
                        break;
                    }
                    break;
                }
                case "teleport": {
                    if (actionVal != null) {
                        Location loc = null;
                        switch (actionVal) {
                            case "inside": {
                                loc = clanHall.getOwnerLocation();
                                break;
                            }
                            case "outside": {
                                loc = clanHall.getBanishLocation();
                                break;
                            }
                            default: {
                                loc = player.getLocation();
                                break;
                            }
                        }
                        player.teleToLocation((ILocational)loc);
                        break;
                    }
                    break;
                }
                case "give": {
                    if (player.getTarget() != null && player.getTarget().getActingPlayer() != null) {
                        final Clan targetClan = player.getTarget().getActingPlayer().getClan();
                        if (targetClan == null || targetClan.getHideoutId() != 0) {
                            player.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                        }
                        clanHall.setOwner(targetClan);
                        break;
                    }
                    player.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                    break;
                }
                case "take": {
                    final Clan clan = clanHall.getOwner();
                    if (clan != null) {
                        clanHall.setOwner((Clan)null);
                        break;
                    }
                    player.sendMessage("You cannot take Clan Hall which don't have any owner.");
                    break;
                }
                case "cancelFunc": {
                    final ResidenceFunctionData function = clanHall.getFunction(Integer.parseInt(actionVal));
                    if (function != null) {
                        clanHall.removeFunction(function);
                        this.sendClanHallDetails(player, clanHallId);
                        break;
                    }
                    break;
                }
            }
        }
        else {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, clanHallId));
        }
        this.useAdminCommand(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, clanHallId), player);
    }
    
    private void sendClanHallList(final Player player, final int page, final BypassParser parser) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(player, "data/html/admin/clanhall_list.htm");
        final List<ClanHall> clanHallList = ClanHallManager.getInstance().getClanHalls().stream().sorted(Comparator.comparingLong(AbstractResidence::getId)).collect((Collector<? super Object, ?, List<ClanHall>>)Collectors.toList());
        final PageResult result = PageBuilder.newBuilder((Collection)clanHallList, 4, "bypass -h admin_clanhall").currentPage(page).pageHandler((IPageHandler)NextPrevPageHandler.INSTANCE).formatter((IBypassFormatter)BypassParserFormatter.INSTANCE).style((IHtmlStyle)ButtonsStyle.INSTANCE).bodyHandler((pages, clanHall, sb) -> {
            sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
            sb.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, clanHall.getId(), clanHall.getId()));
            sb.append("</table>");
            sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
            sb.append("<tr>");
            sb.append("<td align=center fixwidth=\"83\">Status:</td>");
            sb.append("<td align=center fixwidth=\"83\"></td>");
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (clanHall.getOwner() == null) ? "<font color=\"00FF00\">Free</font>" : "<font color=\"FF9900\">Owned</font>"));
            sb.append("</tr>");
            sb.append("<tr>");
            sb.append("<td align=center fixwidth=\"83\">Location:</td>");
            sb.append("<td align=center fixwidth=\"83\"></td>");
            sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, clanHall.getId()));
            sb.append("</tr>");
            sb.append("<tr>");
            sb.append("<td align=center fixwidth=\"83\">Detailed Info:</td>");
            sb.append("<td align=center fixwidth=\"83\"></td>");
            sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, clanHall.getId()));
            sb.append("</tr>");
            sb.append("</table>");
            sb.append("<br>");
        }).build();
        html.replace("%pages%", (result.getPages() > 0) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, result.getPagerTemplate()) : "");
        html.replace("%data%", result.getBodyTemplate().toString());
        player.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private void sendClanHallDetails(final Player player, final int clanHallId) {
        final ClanHall clanHall = ClanHallManager.getInstance().getClanHallById(clanHallId);
        if (clanHall != null) {
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
            final StringBuilder sb = new StringBuilder();
            html.setFile(player, "data/html/admin/clanhall_detail.htm");
            html.replace("%clanHallId%", clanHall.getId());
            html.replace("%clanHallOwner%", (clanHall.getOwner() == null) ? "<font color=\"00FF00\">Free</font>" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, clanHall.getOwner().getName()));
            final String grade = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, clanHall.getGrade().toString().replace("GRADE_", ""));
            html.replace("%clanHallGrade%", grade);
            html.replace("%clanHallSize%", clanHall.getGrade().getGradeValue());
            if (!clanHall.getFunctions().isEmpty()) {
                sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
                sb.append("<tr>");
                sb.append("<td align=center fixwidth=\"40\"><font color=\"LEVEL\">ID</font></td>");
                sb.append("<td align=center fixwidth=\"200\"><font color=\"LEVEL\">Type</font></td>");
                sb.append("<td align=center fixwidth=\"40\"><font color=\"LEVEL\">Lvl</font></td>");
                sb.append("<td align=center fixwidth=\"200\"><font color=\"LEVEL\">End date</font></td>");
                sb.append("<td align=center fixwidth=\"100\"><font color=\"LEVEL\">Action</font></td>");
                sb.append("</tr>");
                sb.append("</table>");
                sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=\"363636\">");
                final StringBuilder sb2;
                clanHall.getFunctions().forEach(function -> {
                    sb2.append("<tr>");
                    sb2.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, function.getId()));
                    sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, function.getType().toString()));
                    sb2.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, function.getLevel()));
                    sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, new SimpleDateFormat("dd/MM HH:mm").format(new Date(function.getExpiration()))));
                    sb2.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, clanHallId, function.getId()));
                    sb2.append("</tr>");
                    return;
                });
                sb.append("</table>");
            }
            else {
                sb.append("This Clan Hall doesn't have any Function yet.");
            }
            html.replace("%functionList%", sb.toString());
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        else {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, clanHallId));
            this.useAdminCommand("admin_clanhall", player);
        }
    }
    
    private void processBypass(final Player player, final BypassParser parser) {
        final int page = parser.getInt("page", 0);
        final int clanHallId = parser.getInt("id", 0);
        final String action = parser.getString("action", (String)null);
        final String actionVal = parser.getString("actionVal", (String)null);
        if (clanHallId > 0 && action != null) {
            this.doAction(player, clanHallId, action, actionVal);
        }
        else if (clanHallId > 0) {
            this.sendClanHallDetails(player, clanHallId);
        }
        else {
            this.sendClanHallList(player, page, parser);
        }
    }
    
    public String[] getAdminCommandList() {
        return AdminClanHall.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_clanhall" };
    }
}
