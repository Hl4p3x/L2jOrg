// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.interfaces.IIdentifiable;

public class ItemHolder implements IIdentifiable
{
    private final int id;
    private final long count;
    private final int enchantment;
    
    public ItemHolder(final int id, final long count) {
        this(id, count, 0);
    }
    
    public ItemHolder(final int id, final long count, final int enchantment) {
        this.id = id;
        this.count = count;
        this.enchantment = enchantment;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public int getEnchantment() {
        return this.enchantment;
    }
    
    @Override
    public boolean equals(final Object obj) {
        final ItemHolder objInstance;
        return obj instanceof ItemHolder && (objInstance = (ItemHolder)obj) == obj && (obj == this || (this.id == objInstance.getId() && this.count == objInstance.getCount()));
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IJ)Ljava/lang/String;, this.getClass().getSimpleName(), this.id, this.count);
    }
}
