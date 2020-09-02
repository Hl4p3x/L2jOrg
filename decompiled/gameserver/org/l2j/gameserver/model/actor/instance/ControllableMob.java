// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.ai.ControllableMobAI;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class ControllableMob extends Monster
{
    private boolean _isInvul;
    
    public ControllableMob(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2ControllableMobInstance);
    }
    
    @Override
    public boolean isAggressive() {
        return true;
    }
    
    @Override
    public int getAggroRange() {
        return 500;
    }
    
    @Override
    protected CreatureAI initAI() {
        return new ControllableMobAI(this);
    }
    
    @Override
    public void detachAI() {
    }
    
    @Override
    public boolean isInvul() {
        return this._isInvul;
    }
    
    public void setInvul(final boolean isInvul) {
        this._isInvul = isInvul;
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        this.setAI(null);
        return true;
    }
}
