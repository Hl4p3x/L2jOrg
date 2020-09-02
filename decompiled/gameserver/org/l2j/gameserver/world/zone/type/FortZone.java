// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;

public final class FortZone extends ResidenceZone
{
    public FortZone(final int id) {
        super(id);
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equals("fortId")) {
            this.setResidenceId(Integer.parseInt(value));
        }
        else {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        creature.setInsideZone(ZoneType.FORT, true);
    }
    
    @Override
    protected void onExit(final Creature creature) {
        creature.setInsideZone(ZoneType.FORT, false);
    }
}
