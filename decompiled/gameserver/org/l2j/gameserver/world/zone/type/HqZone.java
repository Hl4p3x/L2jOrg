// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class HqZone extends Zone
{
    public HqZone(final int id) {
        super(id);
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (!"castleId".equals(name)) {
            if (!"fortId".equals(name)) {
                if (!"clanHallId".equals(name)) {
                    if (!"territoryId".equals(name)) {
                        super.setParameter(name, value);
                    }
                }
            }
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (GameUtils.isPlayer(creature)) {
            creature.setInsideZone(ZoneType.HQ, true);
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        if (GameUtils.isPlayer(creature)) {
            creature.setInsideZone(ZoneType.HQ, false);
        }
    }
}
