// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class FatalBlow extends AbstractEffect
{
    private final double power;
    private final double chanceBoost;
    private final double criticalChance;
    private final boolean overHit;
    
    private FatalBlow(final StatsSet params) {
        this.power = params.getDouble("power");
        this.chanceBoost = params.getDouble("chance-boost");
        this.criticalChance = params.getDouble("critical-chance", 0.0);
        this.overHit = params.getBoolean("over-hit", false);
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        return !Formulas.calcPhysicalSkillEvasion(effector, effected, skill) && Formulas.calcBlowSuccess(effector, effected, skill, this.chanceBoost);
    }
    
    public EffectType getEffectType() {
        return EffectType.PHYSICAL_ATTACK;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effector.isAlikeDead()) {
            return;
        }
        if (this.overHit && GameUtils.isAttackable((WorldObject)effected)) {
            ((Attackable)effected).overhitEnabled(true);
        }
        double damage = Formulas.calcBlowDamage(effector, effected, skill, this.power);
        final boolean crit = Formulas.calcCrit(this.criticalChance, effector, effected, skill);
        if (crit) {
            damage *= 2.0;
        }
        effector.doAttack(damage, effected, skill, false, false, true, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new FatalBlow(data);
        }
        
        public String effectName() {
            return "fatal-blow";
        }
    }
}
