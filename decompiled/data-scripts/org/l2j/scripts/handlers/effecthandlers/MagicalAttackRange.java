// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class MagicalAttackRange extends AbstractEffect
{
    private final double power;
    private final double shieldDefPercent;
    
    private MagicalAttackRange(final StatsSet params) {
        this.power = params.getDouble("power");
        this.shieldDefPercent = params.getDouble("shield-defense", 0.0);
    }
    
    public EffectType getEffectType() {
        return EffectType.MAGICAL_ATTACK;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }
        double mDef = effected.getMDef();
        switch (Formulas.calcShldUse(effector, effected)) {
            case 1: {
                mDef += effected.getShldDef() * this.shieldDefPercent / 100.0;
                break;
            }
            case 2: {
                mDef = -1.0;
                break;
            }
        }
        double damage = 1.0;
        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        if (mDef != -1.0) {
            damage = Formulas.calcMagicDam(effector, effected, skill, (double)effector.getMAtk(), this.power, mDef, mcrit);
        }
        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new MagicalAttackRange(data);
        }
        
        public String effectName() {
            return "magical-attack-range";
        }
    }
}
