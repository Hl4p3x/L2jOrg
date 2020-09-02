// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class RegenCPFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        if (!GameUtils.isPlayer(creature)) {
            return 0.0;
        }
        final Player player = creature.getActingPlayer();
        double baseValue = player.getTemplate().getBaseCpRegen(creature.getLevel()) * creature.getLevelMod() * BaseStats.CON.calcBonus(creature);
        if (player.isSitting()) {
            baseValue *= 1.5;
        }
        else if (!player.isMoving()) {
            baseValue *= 1.1;
        }
        else if (player.isRunning()) {
            baseValue *= 0.7;
        }
        return Stat.defaultValue(player, stat, baseValue);
    }
}
