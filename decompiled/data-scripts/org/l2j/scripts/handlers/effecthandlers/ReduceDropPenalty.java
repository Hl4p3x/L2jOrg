// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.ReduceDropType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class ReduceDropPenalty extends AbstractEffect
{
    private final double exp;
    private final double deathPenalty;
    private final ReduceDropType type;
    
    private ReduceDropPenalty(final StatsSet params) {
        this.exp = params.getDouble("experience", 0.0);
        this.deathPenalty = params.getDouble("death-penalty", 0.0);
        this.type = (ReduceDropType)params.getEnum("type", (Class)ReduceDropType.class, (Enum)ReduceDropType.MOB);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        switch (this.type) {
            case MOB: {
                this.reduce(effected, Stat.REDUCE_EXP_LOST_BY_MOB, Stat.REDUCE_DEATH_PENALTY_BY_MOB);
                break;
            }
            case PK: {
                this.reduce(effected, Stat.REDUCE_EXP_LOST_BY_PVP, Stat.REDUCE_DEATH_PENALTY_BY_PVP);
                break;
            }
            case RAID: {
                this.reduce(effected, Stat.REDUCE_EXP_LOST_BY_RAID, Stat.REDUCE_DEATH_PENALTY_BY_RAID);
                break;
            }
            case ANY: {
                this.reduce(effected, Stat.REDUCE_EXP_LOST_BY_MOB, Stat.REDUCE_DEATH_PENALTY_BY_MOB);
                this.reduce(effected, Stat.REDUCE_EXP_LOST_BY_PVP, Stat.REDUCE_DEATH_PENALTY_BY_PVP);
                this.reduce(effected, Stat.REDUCE_EXP_LOST_BY_RAID, Stat.REDUCE_DEATH_PENALTY_BY_RAID);
                break;
            }
        }
    }
    
    private void reduce(final Creature effected, final Stat statExp, final Stat statPenalty) {
        effected.getStats().mergeMul(statExp, this.exp);
        effected.getStats().mergeMul(statPenalty, this.deathPenalty);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ReduceDropPenalty(data);
        }
        
        public String effectName() {
            return "reduce-drop-penalty";
        }
    }
}
