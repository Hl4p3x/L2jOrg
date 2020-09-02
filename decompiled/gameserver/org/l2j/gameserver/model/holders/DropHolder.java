// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.DropType;

public class DropHolder
{
    private final DropType _dropType;
    private final int _itemId;
    private final long _min;
    private final long _max;
    private final double _chance;
    
    public DropHolder(final DropType dropType, final int itemId, final long min, final long max, final double chance) {
        this._dropType = dropType;
        this._itemId = itemId;
        this._min = min;
        this._max = max;
        this._chance = chance;
    }
    
    public DropType getDropType() {
        return this._dropType;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public long getMin() {
        return this._min;
    }
    
    public long getMax() {
        return this._max;
    }
    
    public double getChance() {
        return this._chance;
    }
}
