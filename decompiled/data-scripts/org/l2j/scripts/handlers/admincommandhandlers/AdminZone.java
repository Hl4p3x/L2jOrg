// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.cache.HtmCache;
import java.util.Iterator;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.MapRegionManager;
import java.util.StringTokenizer;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminZone implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player player) {
        if (Objects.isNull(player)) {
            return false;
        }
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        if (actualCommand.equalsIgnoreCase("admin_zone_check")) {
            showHtml(player);
            BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, MapRegionManager.getInstance().getMapRegionX(player.getX()), MapRegionManager.getInstance().getMapRegionY(player.getY()), MapRegionManager.getInstance().getMapRegionLocId((WorldObject)player)));
            getGeoRegionXY(player);
            BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, MapRegionManager.getInstance().getClosestTownName((Creature)player)));
            if (!player.isInInstance()) {
                Location loc = MapRegionManager.getInstance().getTeleToLocation((Creature)player, TeleportWhereType.CASTLE);
                BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, loc.getX(), loc.getY(), loc.getZ()));
                loc = MapRegionManager.getInstance().getTeleToLocation((Creature)player, TeleportWhereType.CLANHALL);
                BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, loc.getX(), loc.getY(), loc.getZ()));
                loc = MapRegionManager.getInstance().getTeleToLocation((Creature)player, TeleportWhereType.SIEGEFLAG);
                BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, loc.getX(), loc.getY(), loc.getZ()));
                loc = MapRegionManager.getInstance().getTeleToLocation((Creature)player, TeleportWhereType.TOWN);
                BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, loc.getX(), loc.getY(), loc.getZ()));
            }
        }
        else if (actualCommand.equalsIgnoreCase("admin_zone_visual")) {
            if (!st.hasMoreTokens()) {
                showHtml(player);
                return true;
            }
            final String next = st.nextToken();
            if (next.equalsIgnoreCase("all")) {
                for (final Zone zone : ZoneManager.getInstance().getZones((ILocational)player)) {
                    zone.visualizeZone(player.getZ());
                }
                for (final SpawnTerritory territory : ZoneManager.getInstance().getSpawnTerritories((WorldObject)player)) {
                    territory.visualizeZone(player.getZ());
                }
                showHtml(player);
            }
            else {
                final int zoneId = Integer.parseInt(next);
                ZoneManager.getInstance().getZoneById(zoneId).visualizeZone(player.getZ());
            }
        }
        else if (actualCommand.equalsIgnoreCase("admin_zone_visual_clear")) {
            ZoneManager.getInstance().clearDebugItems();
            showHtml(player);
        }
        return true;
    }
    
    private static void showHtml(final Player activeChar) {
        final String htmContent = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/zone.htm");
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setHtml(htmContent);
        adminReply.replace("%PEACE%", activeChar.isInsideZone(ZoneType.PEACE) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%PVP%", activeChar.isInsideZone(ZoneType.PVP) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%SIEGE%", activeChar.isInsideZone(ZoneType.SIEGE) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%CASTLE%", activeChar.isInsideZone(ZoneType.CASTLE) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%FORT%", activeChar.isInsideZone(ZoneType.FORT) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%HQ%", activeChar.isInsideZone(ZoneType.HQ) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%CLANHALL%", activeChar.isInsideZone(ZoneType.CLAN_HALL) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%LAND%", activeChar.isInsideZone(ZoneType.LANDING) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%NOLAND%", activeChar.isInsideZone(ZoneType.NO_LANDING) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%NOSUMMON%", activeChar.isInsideZone(ZoneType.NO_SUMMON_FRIEND) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%WATER%", activeChar.isInsideZone(ZoneType.WATER) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%FISHING%", activeChar.isInsideZone(ZoneType.FISHING) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%SWAMP%", activeChar.isInsideZone(ZoneType.SWAMP) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%DANGER%", activeChar.isInsideZone(ZoneType.DANGER_AREA) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%NOSTORE%", activeChar.isInsideZone(ZoneType.NO_STORE) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%SCRIPT%", activeChar.isInsideZone(ZoneType.SCRIPT) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        adminReply.replace("%TAX%", activeChar.isInsideZone(ZoneType.TAX) ? "<font color=\"LEVEL\">YES</font>" : "NO");
        final StringBuilder zones = new StringBuilder(100);
        for (final Zone zone : ZoneManager.getInstance().getZones((ILocational)activeChar)) {
            if (zone.getName() != null) {
                zones.append(zone.getName());
                if (zone.getId() < 300000) {
                    zones.append(" (");
                    zones.append(zone.getId());
                    zones.append(")");
                }
                zones.append("<br1>");
            }
            else {
                zones.append(zone.getId());
            }
            zones.append(" ");
        }
        for (final SpawnTerritory territory : ZoneManager.getInstance().getSpawnTerritories((WorldObject)activeChar)) {
            zones.append(territory.getName());
            zones.append("<br1>");
        }
        adminReply.replace("%ZLIST%", zones.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private static void getGeoRegionXY(final Player activeChar) {
        final int worldX = activeChar.getX();
        final int worldY = activeChar.getY();
        final int geoX = (worldX + 327680 >> 4 >> 11) + 10;
        final int geoY = (worldY + 262144 >> 4 >> 11) + 10;
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, geoX, geoY));
    }
    
    public String[] getAdminCommandList() {
        return AdminZone.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_zone_check", "admin_zone_visual", "admin_zone_visual_clear" };
    }
}
