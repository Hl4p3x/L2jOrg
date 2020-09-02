// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public class RestorationItemHolder
{
    private final int _id;
    private final long _count;
    private final int _minEnchant;
    private final int _maxEnchant;
    
    public RestorationItemHolder(final int id, final long count, final int minEnchant, final int maxEnchant) {
        this._id = id;
        this._count = count;
        this._minEnchant = minEnchant;
        this._maxEnchant = maxEnchant;
    }
    
    public int getId() {
        return this._id;
    }
    
    public long getCount() {
        return this._count;
    }
    
    public int getMinEnchant() {
        return this._minEnchant;
    }
    
    public int getMaxEnchant() {
        return this._maxEnchant;
    }
}
