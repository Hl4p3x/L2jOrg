// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class AbstractStatAddEffect extends AbstractEffect
{
    public final Stat stat;
    public final double amount;
    
    public AbstractStatAddEffect(final StatsSet params, final Stat stat) {
        this.stat = stat;
        this.amount = params.getDouble("amount", 0.0);
    }
    
    public void pump(final Creature effected, final Skill skill) {
        effected.getStats().mergeAdd(this.stat, this.amount);
    }
}
