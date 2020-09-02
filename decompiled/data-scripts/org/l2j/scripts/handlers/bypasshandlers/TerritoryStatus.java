// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class TerritoryStatus implements IBypassHandler
{
    private static final String[] COMMANDS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!GameUtils.isNpc((WorldObject)target)) {
            return false;
        }
        final Npc npc = (Npc)target;
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        if (npc.getCastle().getOwnerId() > 0) {
            html.setFile(player, "data/html/territorystatus.htm");
            final Clan clan = ClanTable.getInstance().getClan(npc.getCastle().getOwnerId());
            html.replace("%clanname%", clan.getName());
            html.replace("%clanleadername%", clan.getLeaderName());
        }
        else {
            html.setFile(player, "data/html/territorynoclan.htm");
        }
        html.replace("%castlename%", npc.getCastle().getName());
        html.replace("%taxpercent%", Integer.toString(npc.getCastle().getTaxPercent(TaxType.BUY)));
        html.replace("%objectId%", String.valueOf(npc.getObjectId()));
        if (npc.getCastle().getId() > 6) {
            html.replace("%territory%", "The Kingdom of Elmore");
        }
        else {
            html.replace("%territory%", "The Kingdom of Aden");
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        return true;
    }
    
    public String[] getBypassList() {
        return TerritoryStatus.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "TerritoryStatus" };
    }
}
