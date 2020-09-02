// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import java.util.Iterator;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminClan implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command);
        final String nextToken;
        final String cmd = nextToken = st.nextToken();
        switch (nextToken) {
            case "admin_clan_info": {
                final Player player = this.getPlayer(activeChar, st);
                if (player == null) {
                    break;
                }
                final Clan clan = player.getClan();
                if (clan == null) {
                    activeChar.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
                    return false;
                }
                final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
                html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/claninfo.htm"));
                html.replace("%clan_name%", clan.getName());
                html.replace("%clan_leader%", clan.getLeaderName());
                html.replace("%clan_level%", String.valueOf(clan.getLevel()));
                html.replace("%clan_has_castle%", (clan.getCastleId() > 0) ? CastleManager.getInstance().getCastleById(clan.getCastleId()).getName() : "No");
                html.replace("%clan_has_clanhall%", (clan.getHideoutId() > 0) ? ClanHallManager.getInstance().getClanHallById(clan.getHideoutId()).getName() : "No");
                html.replace("%clan_points%", String.valueOf(clan.getReputationScore()));
                html.replace("%clan_players_count%", String.valueOf(clan.getMembersCount()));
                html.replace("%clan_ally%", (clan.getAllyId() > 0) ? clan.getAllyName() : "Not in ally");
                html.replace("%current_player_objectId%", String.valueOf(player.getObjectId()));
                html.replace("%current_player_name%", player.getName());
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
                break;
            }
            case "admin_clan_changeleader": {
                final Player player = this.getPlayer(activeChar, st);
                if (player == null) {
                    break;
                }
                final Clan clan = player.getClan();
                if (clan == null) {
                    activeChar.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
                    return false;
                }
                final ClanMember member = clan.getClanMember(player.getObjectId());
                if (member == null) {
                    break;
                }
                if (player.isAcademyMember()) {
                    player.sendPacket(SystemMessageId.THAT_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER);
                    break;
                }
                clan.setNewLeader(member);
                break;
            }
            case "admin_clan_show_pending": {
                final NpcHtmlMessage html2 = new NpcHtmlMessage(0, 1);
                html2.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/clanchanges.htm"));
                final StringBuilder sb = new StringBuilder();
                for (final Clan clan2 : ClanTable.getInstance().getClans()) {
                    if (clan2.getNewLeaderId() != 0) {
                        sb.append("<tr>");
                        sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, clan2.getName()));
                        sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, clan2.getNewLeaderName()));
                        sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, clan2.getId()));
                        sb.append("</tr>");
                    }
                }
                html2.replace("%data%", sb.toString());
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html2 });
                break;
            }
            case "admin_clan_force_pending": {
                if (!st.hasMoreElements()) {
                    break;
                }
                final String token = st.nextToken();
                if (!Util.isInteger(token)) {
                    break;
                }
                final int clanId = Integer.parseInt(token);
                final Clan clan3 = ClanTable.getInstance().getClan(clanId);
                if (clan3 == null) {
                    break;
                }
                final ClanMember member2 = clan3.getClanMember(clan3.getNewLeaderId());
                if (member2 == null) {
                    break;
                }
                clan3.setNewLeader(member2);
                BuilderUtil.sendSysMessage(activeChar, "Task have been forcely executed.");
                break;
            }
        }
        return true;
    }
    
    private Player getPlayer(final Player activeChar, final StringTokenizer st) {
        Player player = null;
        if (st.hasMoreTokens()) {
            final String val = st.nextToken();
            if (Util.isInteger(val)) {
                player = World.getInstance().findPlayer(Integer.parseInt(val));
                if (player == null) {
                    activeChar.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
                    return null;
                }
            }
            else {
                player = World.getInstance().findPlayer(val);
                if (player == null) {
                    activeChar.sendPacket(SystemMessageId.NAME_IS_NOT_ALLOWED_PLEASE_CHOOSE_ANOTHER_NAME);
                    return null;
                }
            }
        }
        else {
            final WorldObject targetObj = activeChar.getTarget();
            if (!GameUtils.isPlayer(targetObj)) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return null;
            }
            player = targetObj.getActingPlayer();
        }
        return player;
    }
    
    public String[] getAdminCommandList() {
        return AdminClan.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_clan_info", "admin_clan_changeleader", "admin_clan_show_pending", "admin_clan_force_pending" };
    }
}
