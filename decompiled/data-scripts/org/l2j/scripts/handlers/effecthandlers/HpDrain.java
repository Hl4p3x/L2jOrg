// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class HpDrain extends AbstractEffect
{
    private final double power;
    private final double percentage;
    
    private HpDrain(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.percentage = params.getDouble("percentage", 0.0);
    }
    
    public EffectType getEffectType() {
        return EffectType.HP_DRAIN;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effector.isAlikeDead()) {
            return;
        }
        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        final double damage = Formulas.calcMagicDam(effector, effected, skill, (double)effector.getMAtk(), this.power, (double)effected.getMDef(), mcrit);
        final int cp = (int)effected.getCurrentCp();
        final int hp = (int)effected.getCurrentHp();
        double drain;
        if (cp > 0) {
            drain = ((damage < cp) ? 0.0 : (damage - cp));
        }
        else if (damage > hp) {
            drain = hp;
        }
        else {
            drain = damage;
        }
        final double hpAdd = this.percentage / 100.0 * drain;
        final double hpFinal = (effector.getCurrentHp() + hpAdd > effector.getMaxHp()) ? effector.getMaxHp() : (effector.getCurrentHp() + hpAdd);
        effector.setCurrentHp(hpFinal);
        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new HpDrain(data);
        }
        
        public String effectName() {
            return "hp-drain";
        }
    }
}
