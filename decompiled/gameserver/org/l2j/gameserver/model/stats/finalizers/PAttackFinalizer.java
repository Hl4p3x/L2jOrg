// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.item.BodyPart;
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

public class PAttackFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = this.calcWeaponBaseValue(creature, stat);
        if (GameUtils.isPlayer(creature) && Objects.nonNull(creature.getActiveWeaponInstance())) {
            baseValue += this.calcEnchantPAtkBonus(creature.getActiveWeaponInstance());
        }
        if (Config.CHAMPION_ENABLE && creature.isChampion()) {
            baseValue *= Config.CHAMPION_ATK;
        }
        if (creature.isRaid()) {
            baseValue *= Config.RAID_PATTACK_MULTIPLIER;
        }
        final double strBonus = (creature.getSTR() > 0) ? BaseStats.STR.calcBonus(creature) : 1.0;
        baseValue *= strBonus * creature.getLevelMod();
        return Math.min(Stat.defaultValue(creature, stat, baseValue), Config.MAX_PATK);
    }
    
    private double calcEnchantPAtkBonus(final Item item) {
        final int enchant = item.getEnchantLevel();
        final boolean hasTwoHandBonus = item.getBodyPart() == BodyPart.TWO_HAND && item.getItemType() != WeaponType.SPEAR;
        final boolean isRanged = item.getItemType().isRanged();
        double n = 0.0;
        switch (item.getCrystalType()) {
            case S: {
                n = this.calcEnchantPAtkCrystalS(enchant, hasTwoHandBonus, isRanged);
                break;
            }
            case A:
            case B:
            case C:
            case D: {
                n = this.calcEnchantPAtkCrystalDefault(enchant, hasTwoHandBonus, isRanged);
                break;
            }
            default: {
                n = 0.0;
                break;
            }
        }
        return n;
    }
    
    private int calcEnchantPAtkCrystalDefault(final int enchant, final boolean hasTwoHandBonus, final boolean isRanged) {
        final int bonus = isRanged ? 8 : (hasTwoHandBonus ? 5 : 4);
        return Math.min(enchant, 3) * bonus + (Math.max(0, enchant - 3) * bonus << 1);
    }
    
    private int calcEnchantPAtkCrystalS(final int enchant, final boolean hasTwoHandBonus, final boolean isRanged) {
        final int bonus = isRanged ? 10 : (hasTwoHandBonus ? 6 : 5);
        final int secBonus = isRanged ? 126 : (hasTwoHandBonus ? 77 : 43);
        return Math.min(enchant, 3) * bonus + (Math.min(Math.max(0, enchant - 3), 13) * bonus << 2) + Math.max(0, enchant - 16) * secBonus;
    }
}
