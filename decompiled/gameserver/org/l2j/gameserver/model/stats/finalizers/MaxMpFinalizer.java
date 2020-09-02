// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class MaxMpFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = this.calcWeaponPlusBaseValue(creature, stat);
        if (GameUtils.isPet(creature)) {
            final Pet pet = (Pet)creature;
            baseValue += pet.getPetLevelData().getPetMaxMP();
        }
        else if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (player != null) {
                baseValue += player.getTemplate().getBaseMpMax(player.getLevel());
            }
        }
        final double menBonus = (creature.getMEN() > 0) ? BaseStats.MEN.calcBonus(creature) : 1.0;
        baseValue *= menBonus;
        return Stat.defaultValue(creature, stat, baseValue);
    }
}
