// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class NoSummonFriendZone extends Zone
{
    public NoSummonFriendZone(final int id) {
        super(id);
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true);
    }
    
    @Override
    protected void onExit(final Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false);
    }
}
