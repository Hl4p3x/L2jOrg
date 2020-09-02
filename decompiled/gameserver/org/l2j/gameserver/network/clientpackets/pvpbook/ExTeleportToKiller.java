// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pvpbook;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExTeleportToKiller extends ClientPacket
{
    private String killerName;
    
    @Override
    protected void readImpl() throws Exception {
        this.killerName = this.readSizedString();
    }
    
    @Override
    protected void runImpl() {
        final Player killer = World.getInstance().findPlayer(this.killerName);
        if (Objects.isNull(killer)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_LOCATE_THE_SELECTED_FOE_THE_FOE_IS_NOT_ONLINE);
            return;
        }
        if (killer.isInsideZone(ZoneType.PEACE) || killer.isInsideZone(ZoneType.SIEGE) || killer.isInOlympiadMode()) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (player.getRevengeUsableTeleport() > 0 && player.reduceAdena("Teleport To Killer", 140000L, player, true)) {
            player.useRevengeTeleport();
            CommonSkill.HIDE.getSkill().applyEffects(player, player);
            player.teleToLocation(killer.getLocation());
        }
    }
}
