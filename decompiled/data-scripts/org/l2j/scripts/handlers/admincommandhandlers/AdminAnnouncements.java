// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.data.AnnounceData;
import org.l2j.gameserver.data.database.announce.AnnouncementType;
import org.l2j.gameserver.data.database.announce.Announce;
import java.util.Objects;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminAnnouncements implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player gm) {
        final StringTokenizer st = new StringTokenizer(command);
        final String s;
        final String cmd = s = (st.hasMoreTokens() ? st.nextToken() : "");
        switch (s) {
            case "admin_announce":
            case "admin_announce_crit":
            case "admin_announce_screen": {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(gm, "Syntax: //announce <text to announce here>");
                    return false;
                }
                this.doAnnouncement(gm, st, cmd);
                break;
            }
            case "admin_announces": {
                final String subCmd = st.hasMoreTokens() ? st.nextToken() : "";
                return this.manageAnnouncements(gm, st, subCmd);
            }
        }
        return false;
    }
    
    private boolean manageAnnouncements(final Player gm, final StringTokenizer st, final String subCmd) {
        boolean b = false;
        switch (subCmd) {
            case "add": {
                b = this.addAnnouncements(gm, st);
                break;
            }
            case "edit": {
                b = this.editAnnounce(gm, st);
                break;
            }
            case "remove": {
                b = this.removeAnnounce(gm, st);
                break;
            }
            case "restart": {
                b = this.restartAnnounce(gm, st);
                break;
            }
            case "show": {
                b = this.showAnnounce(gm, st);
                break;
            }
            case "list": {
                b = this.listAnnounces(gm, st);
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    private boolean showAnnounce(final Player gm, final StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces show <announcement id>");
            return false;
        }
        final int id = Util.parseNextInt(st, 0);
        final Announce announce = AnnouncementsManager.getInstance().getAnnounce(id);
        if (Objects.nonNull(announce)) {
            this.showAnnounce(gm, announce, "data/html/admin/announces-show.htm");
            return true;
        }
        BuilderUtil.sendSysMessage(gm, "Announcement does not exist!");
        return this.useAdminCommand("admin_announces list", gm);
    }
    
    private boolean restartAnnounce(final Player gm, final StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            AnnouncementsManager.getInstance().restartAutoAnnounce();
            BuilderUtil.sendSysMessage(gm, "Auto announcements has been successfully restarted.");
            return true;
        }
        final int id = Util.parseNextInt(st, 0);
        final Announce announce = AnnouncementsManager.getInstance().getAnnounce(id);
        if (Objects.nonNull(announce)) {
            if (AnnouncementType.isAutoAnnounce(announce)) {
                final AnnounceData autoAnnounce = (AnnounceData)announce;
                AnnouncementsManager.getInstance().scheduleAnnounce(autoAnnounce);
                BuilderUtil.sendSysMessage(gm, "Auto announcement has been successfully restarted.");
                return true;
            }
            BuilderUtil.sendSysMessage(gm, "This option has effect only on auto announcements!");
        }
        else {
            BuilderUtil.sendSysMessage(gm, "Announcement does not exist!");
        }
        return false;
    }
    
    private boolean removeAnnounce(final Player gm, final StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces remove <announcement id>");
            return false;
        }
        final int id = Util.parseNextInt(st, 0);
        if (AnnouncementsManager.getInstance().deleteAnnouncement(id)) {
            BuilderUtil.sendSysMessage(gm, "Announcement has been successfully removed!");
        }
        else {
            BuilderUtil.sendSysMessage(gm, "Announcement does not exist!");
        }
        return this.useAdminCommand("admin_announces list", gm);
    }
    
    private boolean editAnnounce(final Player gm, final StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces edit <id>");
            return false;
        }
        final int id = Util.parseNextInt(st, 0);
        final Announce announce2;
        final Announce announce = announce2 = AnnouncementsManager.getInstance().getAnnounce(id);
        final AnnounceData announceData;
        if (!(announce2 instanceof AnnounceData) || (announceData = (AnnounceData)announce2) != (AnnounceData)announce2) {
            BuilderUtil.sendSysMessage(gm, "Announcement does not exist!");
            return false;
        }
        if (!st.hasMoreTokens()) {
            this.showAnnounce(gm, announce, "data/html/admin/announces-edit.htm");
            return true;
        }
        final AnnouncementType type = AnnouncementType.findByName(st.nextToken());
        if (type == AnnouncementType.EVENT) {
            BuilderUtil.sendSysMessage(gm, "You can't edit event's announcements!");
            return false;
        }
        final int tokens = st.countTokens();
        if (tokens < 4) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces edit <id> <type> <initial_delay> <delay> <repeat> <text>");
            return false;
        }
        final String initialDelay = st.nextToken();
        final String delay = st.nextToken();
        final String repeatToken = st.nextToken();
        if (!Util.isInteger(delay) || !Util.isInteger(repeatToken) || !Util.isInteger(initialDelay)) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces edit <id> <type> <initial_delay> <delay> <repeat> <text>");
            return false;
        }
        final StringBuilder content = this.getContent(st);
        int repeat = Integer.parseInt(repeatToken);
        if (repeat <= 0) {
            repeat = -1;
        }
        announceData.setAuthor(gm.getName());
        announceData.setDelay(Integer.parseInt(delay) * 1000L);
        announceData.setInitial(Integer.parseInt(initialDelay) * 1000L);
        announceData.setRepeat(repeat);
        announceData.setType(type);
        if (content.length() > 0) {
            announceData.setContent(content.toString());
        }
        AnnouncementsManager.getInstance().updateAnnouncement(announce);
        BuilderUtil.sendSysMessage(gm, "Announcement has been successfully edited!");
        return this.useAdminCommand("admin_announces list", gm);
    }
    
    private boolean addAnnouncements(final Player gm, final StringTokenizer st) {
        if (!st.hasMoreTokens()) {
            final String content = HtmCache.getInstance().getHtm(gm, "data/html/admin/announces-add.htm");
            GameUtils.sendCBHtml(gm, content);
            return true;
        }
        final int tokens = st.countTokens();
        if (tokens >= 5) {
            final Announce announce = this.tryCreateAnnounce(gm, st);
            if (Objects.nonNull(announce)) {
                AnnouncementsManager.getInstance().addAnnouncement(announce);
                BuilderUtil.sendSysMessage(gm, "Announcement has been successfully added!");
                return this.useAdminCommand("admin_announces list", gm);
            }
        }
        BuilderUtil.sendSysMessage(gm, "Syntax: //announces add <type> <initial_delay> <delay> <repeat> <text>");
        return false;
    }
    
    private Announce tryCreateAnnounce(final Player gm, final StringTokenizer st) {
        final AnnouncementType type = AnnouncementType.findByName(st.nextToken());
        final String initialDelay = st.nextToken();
        final String delay = st.nextToken();
        final String repeatToken = st.nextToken();
        if (!Util.isInteger(delay) || !Util.isInteger(repeatToken) || !Util.isInteger(initialDelay)) {
            BuilderUtil.sendSysMessage(gm, "Syntax: //announces add <type> <initial_delay> <delay> <repeat> <text>");
            return null;
        }
        final long timeToStart = Integer.parseInt(initialDelay) * 1000L;
        if (timeToStart < 10000L) {
            BuilderUtil.sendSysMessage(gm, "Delay cannot be less then 10 seconds!");
            return null;
        }
        int repeat = Integer.parseInt(repeatToken);
        if (repeat <= 0) {
            repeat = -1;
        }
        final StringBuilder contentBuilder = this.getContent(st);
        return (Announce)new AnnounceData(type, contentBuilder.toString(), gm.getName(), timeToStart, Integer.parseInt(delay) * 1000L, repeat);
    }
    
    private StringBuilder getContent(final StringTokenizer st) {
        final StringBuilder contentBuilder = new StringBuilder(st.nextToken());
        while (st.hasMoreTokens()) {
            contentBuilder.append(" ").append(st.nextToken());
        }
        return contentBuilder;
    }
    
    private void showAnnounce(final Player gm, final Announce announce, final String htmlTemplate) {
        String content = HtmCache.getInstance().getHtm(gm, htmlTemplate);
        final String announcementType = announce.getType().name();
        String announcementInitial = "0";
        String announcementDelay = "0";
        String announcementRepeat = "0";
        if (AnnouncementType.isAutoAnnounce(announce)) {
            final AnnounceData autoAnnounce = (AnnounceData)announce;
            announcementInitial = Long.toString(autoAnnounce.getInitial() / 1000L);
            announcementDelay = Long.toString(autoAnnounce.getDelay() / 1000L);
            announcementRepeat = Integer.toString(autoAnnounce.getRepeat());
        }
        content = content.replaceAll("%id%", Integer.toString(announce.getId())).replaceAll("%type%", announcementType).replaceAll("%initial%", announcementInitial).replaceAll("%delay%", announcementDelay).replaceAll("%repeat%", announcementRepeat).replaceAll("%author%", announce.getAuthor()).replaceAll("%content%", announce.getContent());
        GameUtils.sendCBHtml(gm, content);
    }
    
    private boolean listAnnounces(final Player gm, final StringTokenizer st) {
        final int page = Util.parseNextInt(st, 0);
        String content = HtmCache.getInstance().getHtm(gm, "data/html/admin/announces-list.htm");
        final PageResult result = PageBuilder.newBuilder(AnnouncementsManager.getInstance().getAllAnnouncements(), 8, "bypass admin_announces list").currentPage(page).bodyHandler((pages, announcement, sb) -> {
            final int id = announcement.getId();
            sb.append("<tr><td width=5></td><td width=80>").append(id).append("</td><td width=100>").append(announcement.getType()).append("</td><td width=100>").append(announcement.getAuthor()).append("</td><td width=60>").append(this.createButton("admin_announces show", id, "show")).append("</td><td width=60>").append(this.createButton("admin_announces remove", id, "remove")).append("</td>");
            if (announcement.getType() != AnnouncementType.EVENT) {
                sb.append("<td width=60>").append(this.createButton("admin_announces edit", id, "edit")).append("</td>");
            }
            else {
                sb.append("<td width=60></td>");
            }
            if (AnnouncementType.isAutoAnnounce(announcement)) {
                sb.append("<td width=60>").append(this.createButton("admin_announces restart", id, "restart")).append("</td>");
            }
            else {
                sb.append("<td width=60></td>");
            }
            sb.append("<td width=5></td></tr>");
        }).build();
        content = content.replaceAll("%pages%", result.getPagerTemplate().toString());
        content = content.replaceAll("%announcements%", result.getBodyTemplate().toString());
        GameUtils.sendCBHtml(gm, content);
        return true;
    }
    
    private String createButton(final String bypass, final int id, final String name) {
        return String.format("<button action=\"bypass -h %s %d\" width=60 height=21>%s</button>", bypass, id, name);
    }
    
    private void doAnnouncement(final Player activeChar, final StringTokenizer st, final String cmd) {
        final StringBuilder announceBuilder = this.getContent(st);
        if (cmd.equals("admin_announce_screen")) {
            Broadcast.toAllOnlinePlayersOnScreen(announceBuilder.toString());
        }
        else {
            if (Config.GM_ANNOUNCER_NAME) {
                announceBuilder.append("[").append(activeChar.getName()).append("]");
            }
            Broadcast.toAllOnlinePlayers(announceBuilder.toString(), cmd.equals("admin_announce_crit"));
        }
        AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
    }
    
    public String[] getAdminCommandList() {
        return AdminAnnouncements.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_announce", "admin_announce_crit", "admin_announce_screen", "admin_announces" };
    }
}
