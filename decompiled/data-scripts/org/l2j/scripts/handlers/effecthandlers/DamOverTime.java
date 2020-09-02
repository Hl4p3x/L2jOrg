// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class DamOverTime extends AbstractEffect
{
    private final boolean canKill;
    private final double power;
    private final StatModifierType mode;
    
    private DamOverTime(final StatsSet params) {
        this.canKill = params.getBoolean("can-kill", false);
        this.power = params.getDouble("power");
        this.mode = (StatModifierType)params.getEnum("mode", (Class)StatModifierType.class);
        this.setTicks(params.getInt("ticks"));
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!skill.isToggle() && skill.isMagic()) {
            final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
            if (mcrit) {
                double damage = this.power * 10.0;
                if (!this.canKill && damage >= effected.getCurrentHp() - 1.0) {
                    damage = effected.getCurrentHp() - 1.0;
                }
                effected.reduceCurrentHp(damage, effector, skill, true, false, true, false, DamageInfo.DamageType.OTHER);
            }
        }
    }
    
    public EffectType getEffectType() {
        return EffectType.DMG_OVER_TIME;
    }
    
    public boolean onActionTime(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead()) {
            return false;
        }
        double damage = this.power * this.getTicksMultiplier() * ((this.mode == StatModifierType.PER) ? effected.getCurrentHp() : 1.0);
        if (damage >= effected.getCurrentHp() - 1.0) {
            if (skill.isToggle()) {
                effected.sendPacket(SystemMessageId.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP);
                return false;
            }
            if (!this.canKill) {
                if (effected.getCurrentHp() <= 1.0) {
                    return false;
                }
                damage = effected.getCurrentHp() - 1.0;
            }
        }
        effector.doAttack(damage, effected, skill, true, false, false, false);
        return skill.isToggle();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new DamOverTime(data);
        }
        
        public String effectName() {
            return "damage-over-time";
        }
    }
}
