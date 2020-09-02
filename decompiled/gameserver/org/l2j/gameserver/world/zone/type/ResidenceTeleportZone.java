// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;

public class ResidenceTeleportZone extends SpawnZone
{
    private int residenceId;
    
    public ResidenceTeleportZone(final int id) {
        super(id);
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equals("residenceId")) {
            this.residenceId = Integer.parseInt(value);
        }
        else {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true);
    }
    
    @Override
    protected void onExit(final Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false);
    }
    
    @Override
    public void oustAllPlayers() {
        this.forEachPlayer(p -> p.teleToLocation(this.getSpawnLoc(), 200));
    }
    
    public int getResidenceId() {
        return this.residenceId;
    }
}
