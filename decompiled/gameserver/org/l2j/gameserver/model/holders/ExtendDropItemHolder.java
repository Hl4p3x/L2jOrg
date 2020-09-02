// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public class ExtendDropItemHolder extends ItemHolder
{
    private final long _maxCount;
    private final double _chance;
    private final double _additionalChance;
    
    public ExtendDropItemHolder(final int id, final long count, final long maxCount, final double chance, final double additionalChance) {
        super(id, count);
        this._maxCount = maxCount;
        this._chance = chance;
        this._additionalChance = additionalChance;
    }
    
    public long getMaxCount() {
        return this._maxCount;
    }
    
    public double getChance() {
        return this._chance;
    }
    
    public double getAdditionalChance() {
        return this._additionalChance;
    }
}
