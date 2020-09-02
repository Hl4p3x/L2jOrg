// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.primeshop;

import org.l2j.gameserver.model.holders.ItemHolder;

public class PrimeShopItem extends ItemHolder
{
    private final int _weight;
    private final int _isTradable;
    
    public PrimeShopItem(final int itemId, final int count, final int weight, final int isTradable) {
        super(itemId, count);
        this._weight = weight;
        this._isTradable = isTradable;
    }
    
    public int getWeight() {
        return this._weight;
    }
    
    public int isTradable() {
        return this._isTradable;
    }
}
