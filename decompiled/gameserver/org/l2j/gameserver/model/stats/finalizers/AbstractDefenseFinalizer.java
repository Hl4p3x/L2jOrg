// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.ToDoubleFunction;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.IStatsFunction;

public abstract class AbstractDefenseFinalizer implements IStatsFunction
{
    protected double calcEnchantDefBonus(final Item item) {
        return (item.getCrystalType() == CrystalType.S) ? this.calcEnchantPdefCrystalS(item.getEnchantLevel()) : this.calcEnchantPdefCrystalDefault(item.getEnchantLevel());
    }
    
    private double calcEnchantPdefCrystalDefault(final int enchant) {
        return Math.min(enchant, 3) + Math.max(0, enchant - 3) * 3;
    }
    
    private double calcEnchantPdefCrystalS(final int enchant) {
        return (Math.min(enchant, 3) << 1) + (Math.min(Math.max(0, enchant - 3), 10) << 3) + Math.max(0, enchant - 10) * 26;
    }
    
    protected ToDoubleFunction<Player> baseDefBySlot(final InventorySlot slot) {
        return player -> player.getTransformation().map(t -> t.getBaseDefBySlot(player, slot)).orElseGet(() -> player.getTemplate().getBaseDefBySlot(slot));
    }
    
    protected double defaultValue(final Creature creature, final Stat stat, final double baseValue) {
        final double mul = Math.max(creature.getStats().getMul(stat), 0.5);
        final double add = creature.getStats().getAdd(stat);
        return baseValue * mul + add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType());
    }
}
