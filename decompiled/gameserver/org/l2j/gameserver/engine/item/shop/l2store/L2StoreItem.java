// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.shop.l2store;

import org.l2j.gameserver.model.holders.ItemHolder;

public class L2StoreItem extends ItemHolder
{
    private final int weight;
    private final boolean tradable;
    
    public L2StoreItem(final int itemId, final int count, final int weight, final boolean isTradable) {
        super(itemId, count);
        this.weight = weight;
        this.tradable = isTradable;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public boolean isTradable() {
        return this.tradable;
    }
}
