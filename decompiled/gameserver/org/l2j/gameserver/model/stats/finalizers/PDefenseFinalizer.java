// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.Config;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;

public class PDefenseFinalizer extends AbstractDefenseFinalizer
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0.0);
        if (GameUtils.isPet(creature)) {
            final Pet pet = (Pet)creature;
            baseValue = pet.getPetLevelData().getPetPDef();
        }
        final Inventory inv = creature.getInventory();
        if (Objects.nonNull(inv)) {
            for (final InventorySlot slot : InventorySlot.armors()) {
                final Item item = inv.getPaperdollItem(slot);
                if (Objects.nonNull(item)) {
                    baseValue += item.getStats(stat, 0);
                    baseValue -= GameUtils.calcIfIsPlayer(creature, this.baseDefBySlot(slot));
                    baseValue += GameUtils.calcIfIsPlayer(creature, player -> this.calcEnchantDefBonus(item));
                }
                else {
                    if (slot != InventorySlot.LEGS || !Util.falseIfNullOrElse((Object)inv.getPaperdollItem(InventorySlot.CHEST), chest -> chest.getBodyPart() == BodyPart.FULL_ARMOR)) {
                        continue;
                    }
                    baseValue -= GameUtils.calcIfIsPlayer(creature, this.baseDefBySlot(slot));
                }
            }
        }
        if (creature.isRaid()) {
            baseValue *= Config.RAID_PDEFENCE_MULTIPLIER;
        }
        if (creature.getLevel() > 0) {
            baseValue *= creature.getLevelMod();
        }
        return this.defaultValue(creature, stat, baseValue);
    }
}
