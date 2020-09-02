// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class ShotsBonusFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = 1.0;
        final Player player = creature.getActingPlayer();
        if (player != null) {
            final Item weapon = player.getActiveWeaponInstance();
            if (weapon != null && weapon.isEnchanted()) {
                baseValue += weapon.getEnchantLevel() * 0.7 / 100.0;
            }
        }
        return Stat.defaultValue(creature, stat, CommonUtil.constrain(baseValue, 1.0, 1.21));
    }
}
