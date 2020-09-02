// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class MAttackSpeedFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = this.calcWeaponBaseValue(creature, stat);
        if (Config.CHAMPION_ENABLE && creature.isChampion()) {
            baseValue *= Config.CHAMPION_SPD_ATK;
        }
        final double witBonus = (creature.getWIT() > 0) ? BaseStats.WIT.calcBonus(creature) : 1.0;
        baseValue *= witBonus;
        return this.validateValue(creature, this.defaultValue(creature, stat, baseValue), 1.0, Config.MAX_MATK_SPEED);
    }
    
    private double defaultValue(final Creature creature, final Stat stat, final double baseValue) {
        final double mul = Math.max(creature.getStats().getMul(stat), 0.7);
        final double add = creature.getStats().getAdd(stat);
        return baseValue * mul + add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType());
    }
}
