// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.util.GeoUtils;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.engine.geo.GeoEngine;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminGeodata implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        final String lowerCase = actualCommand.toLowerCase();
        switch (lowerCase) {
            case "admin_geo_pos": {
                final int worldX = activeChar.getX();
                final int worldY = activeChar.getY();
                final int worldZ = activeChar.getZ();
                final int geoX = GeoEngine.getGeoX(worldX);
                final int geoY = GeoEngine.getGeoY(worldY);
                if (GeoEngine.getInstance().hasGeoPos(geoX, geoY)) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(IIIIIS)Ljava/lang/String;, worldX, worldY, worldZ, geoX, geoY, GeoEngine.getInstance().getHeight(worldX, worldY, worldZ)));
                    break;
                }
                BuilderUtil.sendSysMessage(activeChar, "There is no geodata at this position.");
                break;
            }
            case "admin_geo_spawn_pos": {
                final int worldX = activeChar.getX();
                final int worldY = activeChar.getY();
                final int worldZ = activeChar.getZ();
                final int geoX = GeoEngine.getGeoX(worldX);
                final int geoY = GeoEngine.getGeoY(worldY);
                if (GeoEngine.getInstance().hasGeoPos(geoX, geoY)) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(IIIIIS)Ljava/lang/String;, worldX, worldY, worldZ, geoX, geoY, GeoEngine.getInstance().getHeight(worldX, worldY, worldZ)));
                    break;
                }
                BuilderUtil.sendSysMessage(activeChar, "There is no geodata at this position.");
                break;
            }
            case "admin_geo_can_move": {
                final WorldObject target = activeChar.getTarget();
                if (target == null) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    break;
                }
                if (GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, target)) {
                    BuilderUtil.sendSysMessage(activeChar, "Can move beeline.");
                    break;
                }
                BuilderUtil.sendSysMessage(activeChar, "Can not move beeline!");
                break;
            }
            case "admin_geo_can_see": {
                final WorldObject target = activeChar.getTarget();
                if (target == null) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    break;
                }
                if (GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, target)) {
                    BuilderUtil.sendSysMessage(activeChar, "Can see target.");
                    break;
                }
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.CANNOT_SEE_TARGET) });
                break;
            }
            case "admin_geogrid": {
                GeoUtils.debugGrid(activeChar);
                break;
            }
            case "admin_geomap": {
                final int x = (activeChar.getX() + 294912 >> 15) + 11;
                final int y = (activeChar.getY() + 262144 >> 15) + 10;
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(IIIIII)Ljava/lang/String;, x, y, (x - 20) * 32768, (y - 18) * 32768, (x - 20) * 32768 + 32768 - 1, (y - 18) * 32768 + 32768 - 1));
                break;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminGeodata.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_geo_pos", "admin_geo_spawn_pos", "admin_geo_can_move", "admin_geo_can_see", "admin_geogrid", "admin_geomap" };
    }
}
