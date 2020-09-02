// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.scripts.handlers.effecthandlers.stat.AbstractStatEffect;

public class StatModify extends AbstractStatEffect
{
    private StatModify(final StatsSet params) {
        super(params, (Stat)params.getEnum("stat", (Class)Stat.class), addStat(params));
    }
    
    private static Stat addStat(final StatsSet params) {
        if (params.contains("stat-add")) {
            return (Stat)params.getEnum("stat-add", (Class)Stat.class);
        }
        return (Stat)params.getEnum("stat", (Class)Stat.class);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new StatModify(data);
        }
        
        public String effectName() {
            return "stat-modify";
        }
    }
}
