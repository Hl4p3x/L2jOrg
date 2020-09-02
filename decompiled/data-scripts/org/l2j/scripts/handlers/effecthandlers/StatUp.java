// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class StatUp extends AbstractEffect
{
    private final BaseStats stat;
    private final double power;
    
    private StatUp(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
        this.stat = (BaseStats)params.getEnum("type", (Class)BaseStats.class, (Enum)BaseStats.STR);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        effected.getStats().mergeAdd(this.stat.getStat(), this.power);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new StatUp(data);
        }
        
        public String effectName() {
            return "base-stat";
        }
    }
}
