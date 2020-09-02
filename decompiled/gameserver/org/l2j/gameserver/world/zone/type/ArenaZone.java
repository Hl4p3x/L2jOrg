// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class ArenaZone extends Zone
{
    public ArenaZone(final int id) {
        super(id);
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (GameUtils.isPlayer(creature) && !creature.isInsideZone(ZoneType.PVP)) {
            creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
        }
        creature.setInsideZone(ZoneType.PVP, true);
    }
    
    @Override
    protected void onExit(final Creature creature) {
        if (GameUtils.isPlayer(creature) && creature.isInsideZone(ZoneType.PVP)) {
            creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
        }
        creature.setInsideZone(ZoneType.PVP, false);
    }
}
