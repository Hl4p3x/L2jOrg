// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class VampiricAttack extends AbstractEffect
{
    private final double amount;
    private final double sum;
    
    private VampiricAttack(final StatsSet params) {
        this.amount = params.getDouble("power");
        this.sum = this.amount * params.getDouble("chance");
    }
    
    public void pump(final Creature effected, final Skill skill) {
        effected.getStats().mergeAdd(Stat.ABSORB_DAMAGE_PERCENT, this.amount / 100.0);
        effected.getStats().addToVampiricSum(this.sum);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new VampiricAttack(data);
        }
        
        public String effectName() {
            return "vampiric-attack";
        }
    }
}
