// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class SkillCritical extends AbstractEffect
{
    private final BaseStats stat;
    
    private SkillCritical(final StatsSet params) {
        this.stat = (BaseStats)params.getEnum("stat", (Class)BaseStats.class, (Enum)BaseStats.STR);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        effected.getStats().mergeAdd(Stat.SKILL_CRITICAL, (double)this.stat.ordinal());
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SkillCritical(data);
        }
        
        public String effectName() {
            return "skill-critical";
        }
    }
}
