// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IWriteBoardHandler;

public class RegionBoard implements IWriteBoardHandler
{
    private static final Logger LOGGER;
    private static final int[] REGIONS;
    private static final String[] COMMANDS;
    
    public String[] getCommunityBoardCommands() {
        return RegionBoard.COMMANDS;
    }
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player activeChar) {
        if (command.equals("_bbsloc")) {
            CommunityBoardHandler.getInstance().addBypass(activeChar, "Region", command);
            final String list = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/region_list.html");
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < RegionBoard.REGIONS.length; ++i) {
                final Castle castle = CastleManager.getInstance().getCastleById(i + 1);
                final Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
                String link = list.replaceAll("%region_id%", String.valueOf(i));
                link = link.replace("%region_name%", String.valueOf(RegionBoard.REGIONS[i]));
                link = link.replace("%region_owning_clan%", (clan != null) ? clan.getName() : "NPC");
                link = link.replace("%region_owning_clan_alliance%", (clan != null && clan.getAllyName() != null) ? clan.getAllyName() : "");
                link = link.replace((CharSequence)"%region_tax_rate%", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, castle.getTaxPercent(TaxType.BUY)));
                sb.append(link);
            }
            String html = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/region.html");
            html = html.replace("%region_list%", sb.toString());
            CommunityBoardHandler.separateAndSend(html, activeChar);
        }
        else if (command.startsWith("_bbsloc;")) {
            CommunityBoardHandler.getInstance().addBypass(activeChar, "Region>", command);
            final String id = command.replace("_bbsloc;", "");
            if (!Util.isDigit(id)) {
                RegionBoard.LOGGER.warn("Player {} sent and invalid region bypass {}!", (Object)activeChar, (Object)command);
                return false;
            }
        }
        return true;
    }
    
    public boolean writeCommunityBoardCommand(final Player activeChar, final String arg1, final String arg2, final String arg3, final String arg4, final String arg5) {
        return false;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RegionBoard.class);
        REGIONS = new int[] { 1049, 1052, 1053, 1057, 1060, 1059, 1248, 1247, 1056 };
        COMMANDS = new String[] { "_bbsloc" };
    }
}
