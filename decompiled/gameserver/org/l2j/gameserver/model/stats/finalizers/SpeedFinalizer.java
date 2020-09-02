// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.PetLevelData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.SwampZone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class SpeedFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = this.getBaseSpeed(creature, stat);
        if (GameUtils.isPlayer(creature)) {
            baseValue += this.calcEnchantBodyPart(creature, BodyPart.FEET);
        }
        final byte speedStat = (byte)creature.getStats().getAdd(Stat.STAT_BONUS_SPEED, -1.0);
        if (speedStat >= 0 && speedStat < BaseStats.values().length) {
            final BaseStats baseStat = BaseStats.values()[speedStat];
            final double bonusDex = Math.max(0, baseStat.calcValue(creature) - 55);
            baseValue += bonusDex;
        }
        return this.validateValue(creature, Stat.defaultValue(creature, stat, baseValue), 1.0, Config.MAX_RUN_SPEED);
    }
    
    @Override
    public double calcEnchantBodyPartBonus(final int enchantLevel) {
        return 0.6 * Math.max(enchantLevel - 3, 0) + 0.6 * Math.max(enchantLevel - 6, 0);
    }
    
    private double getBaseSpeed(final Creature creature, final Stat stat) {
        double baseValue = this.calcWeaponPlusBaseValue(creature, stat);
        if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (player.isMounted()) {
                final PetLevelData data = PetDataTable.getInstance().getPetLevelData(player.getMountNpcId(), player.getMountLevel());
                if (data != null) {
                    baseValue = data.getSpeedOnRide(stat);
                    if (player.getMountLevel() - creature.getLevel() >= 10) {
                        baseValue /= 2.0;
                    }
                    if (player.isHungry()) {
                        baseValue /= 2.0;
                    }
                }
            }
        }
        if (GameUtils.isPlayable(creature) && creature.isInsideZone(ZoneType.SWAMP)) {
            final SwampZone zone = ZoneManager.getInstance().getZone(creature, SwampZone.class);
            if (zone != null) {
                baseValue *= zone.getMoveBonus();
            }
        }
        return baseValue;
    }
}
