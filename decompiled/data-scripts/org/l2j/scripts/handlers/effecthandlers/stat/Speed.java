// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.StatsSet;

public final class Speed extends AbstractStatEffect
{
    private Speed(final StatsSet params) {
        super(params, Stat.RUN_SPEED);
    }
    
    @Override
    public void pump(final Creature effected, final Skill skill) {
        switch (this.mode) {
            case DIFF: {
                effected.getStats().mergeAdd(Stat.RUN_SPEED, this.power);
                effected.getStats().mergeAdd(Stat.WALK_SPEED, this.power);
                effected.getStats().mergeAdd(Stat.SWIM_RUN_SPEED, this.power);
                effected.getStats().mergeAdd(Stat.SWIM_WALK_SPEED, this.power);
                effected.getStats().mergeAdd(Stat.FLY_RUN_SPEED, this.power);
                effected.getStats().mergeAdd(Stat.FLY_WALK_SPEED, this.power);
                break;
            }
            case PER: {
                effected.getStats().mergeMul(Stat.RUN_SPEED, this.power);
                effected.getStats().mergeMul(Stat.WALK_SPEED, this.power);
                effected.getStats().mergeMul(Stat.SWIM_RUN_SPEED, this.power);
                effected.getStats().mergeMul(Stat.SWIM_WALK_SPEED, this.power);
                effected.getStats().mergeMul(Stat.FLY_RUN_SPEED, this.power);
                effected.getStats().mergeMul(Stat.FLY_WALK_SPEED, this.power);
                break;
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Speed(data);
        }
        
        public String effectName() {
            return "speed";
        }
    }
}
