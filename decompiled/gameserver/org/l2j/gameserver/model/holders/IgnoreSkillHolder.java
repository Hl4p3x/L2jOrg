// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.concurrent.atomic.AtomicInteger;

public class IgnoreSkillHolder extends SkillHolder
{
    private final AtomicInteger _instances;
    
    public IgnoreSkillHolder(final int skillId, final int skillLevel) {
        super(skillId, skillLevel);
        this._instances = new AtomicInteger(1);
    }
    
    public IgnoreSkillHolder(final SkillHolder holder) {
        super(holder.getSkill());
        this._instances = new AtomicInteger(1);
    }
    
    public int getInstances() {
        return this._instances.get();
    }
    
    public int increaseInstances() {
        return this._instances.incrementAndGet();
    }
    
    public int decreaseInstances() {
        return this._instances.decrementAndGet();
    }
}
