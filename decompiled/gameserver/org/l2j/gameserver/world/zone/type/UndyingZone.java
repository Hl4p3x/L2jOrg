// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class UndyingZone extends Zone
{
    public UndyingZone(final int id) {
        super(id);
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        creature.setInsideZone(ZoneType.UNDYING, true);
    }
    
    @Override
    protected void onExit(final Creature creature) {
        creature.setInsideZone(ZoneType.UNDYING, false);
    }
}
