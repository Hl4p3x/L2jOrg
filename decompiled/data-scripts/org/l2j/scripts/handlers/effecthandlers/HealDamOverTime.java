// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class HealDamOverTime extends AbstractEffect
{
    private final double power;
    
    private HealDamOverTime(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.setTicks(params.getInt("ticks"));
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead()) {
            return false;
        }
        final double healDam = this.power * this.getTicksMultiplier();
        if (healDam > effected.getCurrentHp() && skill.isToggle()) {
            effected.sendPacket(SystemMessageId.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP);
            return false;
        }
        effected.reduceCurrentHp(healDam, effector, skill, true, false, false, false, DamageInfo.DamageType.OTHER);
        return skill.isToggle();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new HealDamOverTime(data);
        }
        
        public String effectName() {
            return "HealDamOverTime";
        }
    }
}
