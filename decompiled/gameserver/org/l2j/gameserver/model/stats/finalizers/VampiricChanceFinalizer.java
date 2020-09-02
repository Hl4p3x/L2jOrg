// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class VampiricChanceFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        final double amount = creature.getStats().getValue(Stat.ABSORB_DAMAGE_PERCENT, 0.0) * 100.0;
        final double vampiricSum = creature.getStats().getVampiricSum();
        return (amount > 0.0) ? Stat.defaultValue(creature, stat, Math.min(1.0, vampiricSum / amount / 100.0)) : 0.0;
    }
}
