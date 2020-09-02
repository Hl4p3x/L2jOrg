// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.instance.Folk;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;

public class FolkStatus extends NpcStatus
{
    public FolkStatus(final Npc activeChar) {
        super(activeChar);
    }
    
    @Override
    public final void reduceHp(final double value, final Creature attacker) {
        this.reduceHp(value, attacker, true, false, false);
    }
    
    @Override
    public final void reduceHp(final double value, final Creature attacker, final boolean awake, final boolean isDOT, final boolean isHpConsumption) {
    }
    
    @Override
    public Folk getOwner() {
        return (Folk)super.getOwner();
    }
}
