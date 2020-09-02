// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine.drop;

public class EventDropItem
{
    private final int _id;
    private final int _min;
    private final int _max;
    private final double _chance;
    
    public EventDropItem(final int id, final int min, final int max, final double chance) {
        this._id = id;
        this._min = min;
        this._max = max;
        this._chance = chance;
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
    
    public double getChance() {
        return this._chance;
    }
}
