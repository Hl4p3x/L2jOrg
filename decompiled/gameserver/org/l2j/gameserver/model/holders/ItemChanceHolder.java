// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.Iterator;
import org.l2j.commons.util.Rnd;
import java.util.List;

public class ItemChanceHolder extends ItemHolder
{
    private final double _chance;
    private final byte _enchantmentLevel;
    private final boolean _maintainIngredient;
    
    public ItemChanceHolder(final int id, final double chance) {
        this(id, chance, 1L);
    }
    
    public ItemChanceHolder(final int id, final double chance, final long count) {
        super(id, count);
        this._chance = chance;
        this._enchantmentLevel = 0;
        this._maintainIngredient = false;
    }
    
    public ItemChanceHolder(final int id, final double chance, final long count, final byte enchantmentLevel) {
        super(id, count);
        this._chance = chance;
        this._enchantmentLevel = enchantmentLevel;
        this._maintainIngredient = false;
    }
    
    public ItemChanceHolder(final int id, final double chance, final long count, final byte enchantmentLevel, final boolean maintainIngredient) {
        super(id, count);
        this._chance = chance;
        this._enchantmentLevel = enchantmentLevel;
        this._maintainIngredient = maintainIngredient;
    }
    
    public static ItemChanceHolder getRandomHolder(final List<ItemChanceHolder> holders) {
        double itemRandom = 100.0 * Rnd.nextDouble();
        for (final ItemChanceHolder holder : holders) {
            if (!Double.isNaN(holder.getChance())) {
                if (holder.getChance() > itemRandom) {
                    return holder;
                }
                itemRandom -= holder.getChance();
            }
        }
        return null;
    }
    
    public double getChance() {
        return this._chance;
    }
    
    public byte getEnchantmentLevel() {
        return this._enchantmentLevel;
    }
    
    public boolean isMaintainIngredient() {
        return this._maintainIngredient;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IJD)Ljava/lang/String;, this.getClass().getSimpleName(), this.getId(), this.getCount(), this._chance);
    }
}
