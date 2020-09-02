// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class AbnormalShield extends AbstractEffect
{
    private final int power;
    
    private AbnormalShield(final StatsSet params) {
        this.power = params.getInt("power", -1);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.setAbnormalShieldBlocks(this.power);
    }
    
    public long getEffectFlags() {
        return EffectFlag.ABNORMAL_SHIELD.getMask();
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.setAbnormalShieldBlocks(Integer.MIN_VALUE);
    }
    
    public EffectType getEffectType() {
        return EffectType.ABNORMAL_SHIELD;
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new AbnormalShield(data);
        }
        
        public String effectName() {
            return "AbnormalShield";
        }
    }
}
