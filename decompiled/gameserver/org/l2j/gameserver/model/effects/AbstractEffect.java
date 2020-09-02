// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.effects;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.Config;
import org.slf4j.Logger;

public abstract class AbstractEffect
{
    protected static final Logger LOGGER;
    private int _ticks;
    
    public int getTicks() {
        return this._ticks;
    }
    
    protected void setTicks(final int ticks) {
        this._ticks = ticks;
    }
    
    public double getTicksMultiplier() {
        return this.getTicks() * Config.EFFECT_TICK_RATIO / 1000.0f;
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return true;
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
    }
    
    public void continuousInstant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        return false;
    }
    
    public long getEffectFlags() {
        return EffectFlag.NONE.getMask();
    }
    
    public boolean checkCondition(final Object obj) {
        return true;
    }
    
    public boolean isInstant() {
        return false;
    }
    
    public boolean canPump(final Creature effector, final Creature effected, final Skill skill) {
        return true;
    }
    
    public void pump(final Creature effected, final Skill skill) {
    }
    
    public EffectType getEffectType() {
        return EffectType.NONE;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AbstractEffect.class);
    }
}
