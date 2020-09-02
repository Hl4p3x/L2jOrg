// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;

public abstract class ResidenceZone extends SpawnZone
{
    private int residenceId;
    
    ResidenceZone(final int id) {
        super(id);
    }
    
    public void banishForeigners(final int owningClanId) {
        this.forEachPlayer(p -> p.teleToLocation(this.getBanishSpawnLoc(), true), p -> p.getClanId() == owningClanId && owningClanId != 0);
    }
    
    public int getResidenceId() {
        return this.residenceId;
    }
    
    void setResidenceId(final int residenceId) {
        this.residenceId = residenceId;
    }
}
