// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.BaseStats;
import java.util.EnumSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class BaseStatsModify extends AbstractEffect
{
    private final double power;
    private final EnumSet<BaseStats> stats;
    
    private BaseStatsModify(final StatsSet data) {
        this.power = data.getDouble("power", 0.0);
        this.stats = (EnumSet<BaseStats>)data.getStringAsEnumSet("types", (Class)BaseStats.class);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        this.stats.forEach(stat -> effected.getStats().mergeAdd(stat.getStat(), this.power));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new BaseStatsModify(data);
        }
        
        public String effectName() {
            return "base-stats";
        }
    }
}
