// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class MaxCpFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0.0);
        final Player player = creature.getActingPlayer();
        if (player != null) {
            baseValue = player.getTemplate().getBaseCpMax(player.getLevel());
        }
        final double conBonus = (creature.getCON() > 0) ? BaseStats.CON.calcBonus(creature) : 1.0;
        baseValue *= conBonus;
        return Stat.defaultValue(creature, stat, baseValue);
    }
}
