// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class ExtractableProduct
{
    private final int _id;
    private final int _min;
    private final int _max;
    private final int _chance;
    private final int _minEnchant;
    private final int _maxEnchant;
    
    public ExtractableProduct(final int id, final int min, final int max, final double chance, final int minEnchant, final int maxEnchant) {
        this._id = id;
        this._min = min;
        this._max = max;
        this._chance = (int)chance;
        this._minEnchant = minEnchant;
        this._maxEnchant = maxEnchant;
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getMin() {
        return this._min;
    }
    
    public int getMax() {
        return this._max;
    }
    
    public int getChance() {
        return this._chance;
    }
    
    public int getMinEnchant() {
        return this._minEnchant;
    }
    
    public int getMaxEnchant() {
        return this._maxEnchant;
    }
}
