// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class PCriticalRateFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = this.calcWeaponBaseValue(creature, stat);
        if (GameUtils.isPlayer(creature)) {
            baseValue += this.calcEnchantBodyPart(creature, BodyPart.LEGS);
        }
        final double dexBonus = (creature.getDEX() > 0) ? BaseStats.DEX.calcBonus(creature) : 1.0;
        return this.validateValue(creature, Stat.defaultValue(creature, stat, baseValue * dexBonus * 10.0), 0.0, Config.MAX_PCRIT_RATE);
    }
    
    @Override
    public double calcEnchantBodyPartBonus(final int enchantLevel) {
        return 0.34 * Math.max(enchantLevel - 3, 0) + 0.34 * Math.max(enchantLevel - 6, 0);
    }
}
