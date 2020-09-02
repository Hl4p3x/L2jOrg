// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class MagicalAttack extends AbstractEffect
{
    private final double power;
    private final boolean overHit;
    
    private MagicalAttack(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.overHit = params.getBoolean("over-hit", false);
    }
    
    public EffectType getEffectType() {
        return EffectType.MAGICAL_ATTACK;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effector.isAlikeDead()) {
            return;
        }
        if (GameUtils.isPlayer((WorldObject)effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }
        if (this.overHit && GameUtils.isAttackable((WorldObject)effected)) {
            ((Attackable)effected).overhitEnabled(true);
        }
        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        final double damage = Formulas.calcMagicDam(effector, effected, skill, (double)effector.getMAtk(), this.power, (double)effected.getMDef(), mcrit);
        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new MagicalAttack(data);
        }
        
        public String effectName() {
            return "magical-attack";
        }
    }
}
