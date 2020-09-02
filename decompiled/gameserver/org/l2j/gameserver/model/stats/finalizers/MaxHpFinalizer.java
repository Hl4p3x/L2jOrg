// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import java.util.stream.Stream;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import java.util.function.ToIntFunction;
import org.l2j.gameserver.engine.item.EnchantItemEngine;
import java.util.function.Predicate;
import java.util.function.Function;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class MaxHpFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0.0);
        if (GameUtils.isPet(creature)) {
            final Pet pet = (Pet)creature;
            baseValue = pet.getPetLevelData().getPetMaxHP();
        }
        else if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (Objects.nonNull(player)) {
                baseValue = player.getTemplate().getBaseHpMax(player.getLevel());
                final PlayerInventory inventory = player.getInventory();
                final double n = baseValue;
                final Stream<Object> stream = InventorySlot.armorset().stream();
                final PlayerInventory obj = inventory;
                Objects.requireNonNull(obj);
                final Stream<Object> filter = stream.map((Function<? super Object, ?>)obj::getPaperdollItem).filter(Objects::nonNull);
                final EnchantItemEngine instance = EnchantItemEngine.getInstance();
                Objects.requireNonNull(instance);
                baseValue = n + filter.mapToInt((ToIntFunction<? super Object>)instance::getArmorHpBonus).reduce(0, Integer::sum);
            }
        }
        final double conBonus = (creature.getCON() > 0) ? BaseStats.CON.calcBonus(creature) : 1.0;
        baseValue *= conBonus;
        return Stat.defaultValue(creature, stat, baseValue);
    }
}
