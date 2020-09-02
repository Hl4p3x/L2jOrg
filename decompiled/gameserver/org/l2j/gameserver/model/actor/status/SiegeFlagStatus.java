// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;

public class SiegeFlagStatus extends NpcStatus
{
    public SiegeFlagStatus(final SiegeFlag activeChar) {
        super(activeChar);
    }
    
    @Override
    public void reduceHp(final double value, final Creature attacker) {
        this.reduceHp(value, attacker, true, false, false);
    }
    
    @Override
    public void reduceHp(double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHpConsumption) {
        if (this.getOwner().isAdvancedHeadquarter()) {
            value /= 2.0;
        }
        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
    }
    
    @Override
    public SiegeFlag getOwner() {
        return (SiegeFlag)super.getOwner();
    }
}
