// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.actor.transform.TransformType;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Optional;
import org.l2j.gameserver.model.item.instance.Item;

@FunctionalInterface
public interface IStatsFunction
{
    default double calcEnchantDefBonus(final Item item) {
        final int enchant = item.getEnchantLevel();
        return enchant + 3 * Math.max(0, enchant - 3);
    }
    
    default void throwIfPresent(final Optional<Double> base) {
        if (base.isPresent()) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        }
    }
    
    default double calcEnchantBodyPart(final Creature creature, final BodyPart... parts) {
        double value = 0.0;
        for (final BodyPart part : parts) {
            final Item item = creature.getInventory().getItemByBodyPart(part);
            if (item != null && item.getEnchantLevel() >= 4) {
                value += this.calcEnchantBodyPartBonus(item.getEnchantLevel());
            }
        }
        return value;
    }
    
    default double calcEnchantBodyPartBonus(final int enchantLevel) {
        return 0.0;
    }
    
    default double calcWeaponBaseValue(final Creature creature, final Stat stat) {
        final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0.0);
        double baseValue = creature.getTransformation().map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);
        if (GameUtils.isPet(creature)) {
            final Pet pet = (Pet)creature;
            final Item weapon = pet.getActiveWeaponInstance();
            final double baseVal = (stat == Stat.PHYSICAL_ATTACK) ? pet.getPetLevelData().getPetPAtk() : ((stat == Stat.MAGIC_ATTACK) ? pet.getPetLevelData().getPetMAtk() : baseTemplateValue);
            baseValue = baseVal + ((weapon != null) ? weapon.getTemplate().getStats(stat, baseVal) : 0.0);
        }
        else if (GameUtils.isPlayer(creature) && (!creature.isTransformed() || creature.getTransformation().get().getType() == TransformType.COMBAT || creature.getTransformation().get().getType() == TransformType.MODE_CHANGE)) {
            final Item weapon2 = creature.getActiveWeaponInstance();
            baseValue = ((weapon2 != null) ? weapon2.getTemplate().getStats(stat, baseTemplateValue) : baseTemplateValue);
        }
        return baseValue;
    }
    
    default double calcWeaponPlusBaseValue(final Creature creature, final Stat stat) {
        final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0.0);
        double baseValue = creature.getTransformation().filter(transform -> !transform.isStance()).map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);
        if (GameUtils.isPlayable(creature)) {
            final Inventory inv = creature.getInventory();
            if (inv != null) {
                baseValue = inv.calcForEachEquippedItem(item -> item.getStats(stat, 0), baseValue, Double::sum);
            }
        }
        return baseValue;
    }
    
    default double calcEnchantedItemBonus(final Creature creature, final Stat stat) {
        if (!GameUtils.isPlayer(creature)) {
            return 0.0;
        }
        return creature.getInventory().calcForEachEquippedItem(item -> this.calcEnchantStatBonus(creature, stat, item), 0.0, Double::sum);
    }
    
    default double calcEnchantStatBonus(final Creature creature, final Stat stat, final Item item) {
        if (!item.isEnchanted()) {
            return 0.0;
        }
        final BodyPart bodyPart = item.getBodyPart();
        if (bodyPart.isAnyOf(BodyPart.HAIR, BodyPart.HAIR2, BodyPart.HAIR_ALL)) {
            if (stat != Stat.PHYSICAL_DEFENCE && stat != Stat.MAGICAL_DEFENCE) {
                return 0.0;
            }
        }
        else if (item.getStats(stat, 0) <= 0.0) {
            return 0.0;
        }
        int enchant = item.getEnchantLevel();
        if (creature.getActingPlayer().isInOlympiadMode() && Config.ALT_OLY_ENCHANT_LIMIT >= 0 && enchant > Config.ALT_OLY_ENCHANT_LIMIT) {
            enchant = Config.ALT_OLY_ENCHANT_LIMIT;
        }
        double calcEnchantDefBonus = 0.0;
        switch (stat) {
            case MAGICAL_DEFENCE:
            case PHYSICAL_DEFENCE: {
                calcEnchantDefBonus = calcEnchantDefBonus(item);
                break;
            }
            default: {
                calcEnchantDefBonus = 0.0;
                break;
            }
        }
        return calcEnchantDefBonus;
    }
    
    default double validateValue(final Creature creature, final double value, final double minValue, final double maxValue) {
        if (value > maxValue && !creature.canOverrideCond(PcCondOverride.MAX_STATS_VALUE)) {
            return maxValue;
        }
        return Math.max(minValue, value);
    }
    
    double calc(final Creature creature, final Optional<Double> base, final Stat stat);
}
