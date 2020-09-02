// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.Config;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class MAttackFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = this.calcWeaponBaseValue(creature, stat);
        if (GameUtils.isPlayer(creature) && Objects.nonNull(creature.getActiveWeaponInstance())) {
            baseValue += this.calcEnchantMAtkBonus(creature.getActiveWeaponInstance());
        }
        if (Config.CHAMPION_ENABLE && creature.isChampion()) {
            baseValue *= Config.CHAMPION_ATK;
        }
        if (creature.isRaid()) {
            baseValue *= Config.RAID_MATTACK_MULTIPLIER;
        }
        final double intBonus = BaseStats.INT.calcBonus(creature);
        baseValue *= Math.pow(intBonus, 2.0) * Math.pow(creature.getLevelMod(), 2.0);
        return Math.min(Stat.defaultValue(creature, stat, baseValue), Config.MAX_MATK);
    }
    
    private double calcEnchantMAtkBonus(final Item item) {
        final int enchant = item.getEnchantLevel();
        double n = 0.0;
        switch (item.getCrystalType()) {
            case S: {
                n = this.calcEnchantMAtkBonusCrystalS(enchant);
                break;
            }
            case A:
            case B:
            case C:
            case D: {
                n = this.calcEnchantMAtkBonusCrystalDefault(enchant);
                break;
            }
            default: {
                n = 0.0;
                break;
            }
        }
        return n;
    }
    
    private int calcEnchantMAtkBonusCrystalDefault(final int enchant) {
        final int bonus = 3;
        return Math.min(enchant, 3) * 3 + Math.max(0, enchant - 3) * 2 * 3;
    }
    
    private int calcEnchantMAtkBonusCrystalS(final int enchant) {
        final int bonus = 4;
        final int secBonus = 43;
        return Math.min(enchant, 3) * 4 + Math.min(Math.max(0, enchant - 3), 13) * 4 * 4 + Math.max(0, enchant - 16) * 43;
    }
}
