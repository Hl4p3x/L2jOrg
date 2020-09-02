// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.world.MapRegionManager;
import java.util.Objects;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.RespawnZone;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class Loc implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        final RespawnZone zone = (RespawnZone)ZoneManager.getInstance().getZone((ILocational)player, (Class)RespawnZone.class);
        int region;
        if (Objects.nonNull(zone)) {
            region = MapRegionManager.getInstance().getRestartRegion(player, (String)zone.getAllRespawnPoints().get(Race.HUMAN)).getLocId();
        }
        else {
            region = MapRegionManager.getInstance().getMapRegionLocId((WorldObject)player);
        }
        SystemMessage sm;
        if (region > 0) {
            sm = SystemMessage.getSystemMessage(region);
            if (sm.getSystemMessageId().getParamCount() == 3) {
                sm.addInt(player.getX());
                sm.addInt(player.getY());
                sm.addInt(player.getZ());
            }
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.CURRENT_LOCATION_S1);
            sm.addString(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, player.getX(), player.getY(), player.getZ()));
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        return true;
    }
    
    public int[] getUserCommandList() {
        return Loc.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 0 };
    }
}
