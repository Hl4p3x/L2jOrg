// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.world.zone.type.SiegeZone;
import org.l2j.gameserver.model.Clan;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class SiegeStatus implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    private static final String INSIDE_SIEGE_ZONE = "Castle Siege in Progress";
    private static final String OUTSIDE_SIEGE_ZONE = "No Castle Siege Area";
    
    public boolean useUserCommand(final int id, final Player player) {
        if (id != SiegeStatus.COMMAND_IDS[0]) {
            return false;
        }
        if (!player.isNoble() || !player.isClanLeader()) {
            player.sendPacket(SystemMessageId.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_OR_EXALTED_CAN_VIEW_THE_SIEGE_STATUS_WINDOW_DURING_A_SIEGE_WAR);
            return false;
        }
        for (final Siege siege : SiegeManager.getInstance().getSieges()) {
            if (!siege.isInProgress()) {
                continue;
            }
            final Clan clan = player.getClan();
            if (!siege.checkIsAttacker(clan) && !siege.checkIsDefender(clan)) {
                continue;
            }
            final SiegeZone siegeZone = siege.getCastle().getZone();
            final StringBuilder sb = new StringBuilder();
            for (final Player member : clan.getOnlineMembers(0)) {
                sb.append("<tr><td width=170>");
                sb.append(member.getName());
                sb.append("</td><td width=100>");
                sb.append(siegeZone.isInsideZone((WorldObject)member) ? "Castle Siege in Progress" : "No Castle Siege Area");
                sb.append("</td></tr>");
            }
            final NpcHtmlMessage html = new NpcHtmlMessage();
            html.setFile(player, "data/html/siege/siege_status.htm");
            html.replace("%kill_count%", clan.getSiegeKills());
            html.replace("%death_count%", clan.getSiegeDeaths());
            html.replace("%member_list%", sb.toString());
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
            return true;
        }
        player.sendPacket(SystemMessageId.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_OR_EXALTED_CAN_VIEW_THE_SIEGE_STATUS_WINDOW_DURING_A_SIEGE_WAR);
        return false;
    }
    
    public int[] getUserCommandList() {
        return SiegeStatus.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 99 };
    }
}
