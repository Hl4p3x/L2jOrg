// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Attackable;

public class AttackableStatus extends NpcStatus
{
    public AttackableStatus(final Attackable activeChar) {
        super(activeChar);
    }
    
    @Override
    public final void reduceHp(final double value, final Creature attacker) {
        this.reduceHp(value, attacker, true, false, false);
    }
    
    @Override
    public final void reduceHp(final double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHpConsumption) {
        if (this.getOwner().isDead()) {
            return;
        }
        if (value > 0.0) {
            if (this.getOwner().isOverhit()) {
                this.getOwner().setOverhitValues(attacker, value);
            }
            else {
                this.getOwner().overhitEnabled(false);
            }
        }
        else {
            this.getOwner().overhitEnabled(false);
        }
        super.reduceHp(value, attacker, awake, isDOT, isHpConsumption);
        if (!this.getOwner().isDead()) {
            this.getOwner().overhitEnabled(false);
        }
    }
    
    @Override
    public boolean setCurrentHp(final double newHp, final boolean broadcastPacket) {
        return super.setCurrentHp(newHp, true);
    }
    
    @Override
    public Attackable getOwner() {
        return (Attackable)super.getOwner();
    }
}
