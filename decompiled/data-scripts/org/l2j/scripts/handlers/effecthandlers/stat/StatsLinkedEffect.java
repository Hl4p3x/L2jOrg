// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class StatsLinkedEffect extends AbstractEffect
{
    public final double power;
    private final Stat stat;
    private final Stat baseStat;
    
    public StatsLinkedEffect(final StatsSet data) {
        this.power = data.getDouble("power", 0.0);
        this.stat = (Stat)data.getEnum("stat", (Class)Stat.class);
        this.baseStat = (Stat)data.getEnum("base-stat", (Class)Stat.class);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        final double base = effected.getStats().getValue(this.baseStat);
        effected.getStats().mergeAdd(this.stat, base * this.power / 100.0);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new StatsLinkedEffect(data);
        }
        
        public String effectName() {
            return "stats-linked";
        }
    }
}
