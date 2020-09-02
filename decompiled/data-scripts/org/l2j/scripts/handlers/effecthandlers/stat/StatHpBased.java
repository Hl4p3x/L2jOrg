// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.StatsSet;

public class StatHpBased extends AbstractConditionalHpEffect
{
    private StatHpBased(final StatsSet params) {
        super(params, (Stat)params.getEnum("stat", (Class)Stat.class));
    }
    
    @Override
    public void pump(final Creature effected, final Skill skill) {
        if (this.conditions.isEmpty() || this.conditions.stream().allMatch(cond -> cond.test(effected, effected, skill))) {
            switch (this.mode) {
                case DIFF: {
                    effected.getStats().mergeAdd(this.addStat, this.power);
                    break;
                }
                case PER: {
                    effected.getStats().mergeMul(this.mulStat, this.power);
                    break;
                }
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new StatHpBased(data);
        }
        
        public String effectName() {
            return "stat-hp-based";
        }
    }
}
