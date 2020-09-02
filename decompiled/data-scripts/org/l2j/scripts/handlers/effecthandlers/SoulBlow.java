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

public final class SoulBlow extends AbstractEffect
{
    private final double power;
    private final double chanceBoost;
    private final boolean overHit;
    
    private SoulBlow(final StatsSet params) {
        this.power = params.getDouble("power");
        this.chanceBoost = params.getDouble("chance-boost");
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
        if (skill.getMaxSoulConsumeCount() > 0 && GameUtils.isPlayer((WorldObject)effector)) {
            final int chargedSouls = Math.min(effector.getActingPlayer().getChargedSouls(), skill.getMaxSoulConsumeCount());
            damage *= 1.0 + chargedSouls * 0.04;
        }
        effector.doAttack(damage, effected, skill, false, false, true, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SoulBlow(data);
        }
        
        public String effectName() {
            return "soul-blow";
        }
    }
}
