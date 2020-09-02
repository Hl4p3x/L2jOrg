// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class MEvasionRateFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = this.calcWeaponPlusBaseValue(creature, stat);
        final int level = creature.getLevel();
        if (GameUtils.isPlayer(creature)) {
            baseValue += Math.sqrt(creature.getWIT()) * 3.0 + level * 2;
            baseValue += this.calcEnchantBodyPart(creature, BodyPart.HEAD);
        }
        else {
            baseValue += Math.sqrt(creature.getWIT()) * 3.0 + level * 2;
            if (level > 69) {
                baseValue += level - 69 + 2;
            }
        }
        return this.validateValue(creature, Stat.defaultValue(creature, stat, baseValue), Double.NEGATIVE_INFINITY, Config.MAX_EVASION);
    }
    
    @Override
    public double calcEnchantBodyPartBonus(final int enchantLevel) {
        return 0.2 * Math.max(enchantLevel - 3, 0) + 0.2 * Math.max(enchantLevel - 6, 0);
    }
}
