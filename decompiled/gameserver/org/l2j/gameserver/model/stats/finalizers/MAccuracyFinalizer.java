// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class MAccuracyFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = this.calcWeaponPlusBaseValue(creature, stat);
        if (GameUtils.isPlayer(creature)) {
            baseValue += this.calcEnchantBodyPart(creature, BodyPart.GLOVES);
        }
        return Stat.defaultValue(creature, stat, baseValue + Math.sqrt(creature.getWIT()) * 3.0 + creature.getLevel() * 2);
    }
    
    @Override
    public double calcEnchantBodyPartBonus(final int enchantLevel) {
        return 0.2 * Math.max(enchantLevel - 3, 0) + 0.2 * Math.max(enchantLevel - 6, 0);
    }
}
