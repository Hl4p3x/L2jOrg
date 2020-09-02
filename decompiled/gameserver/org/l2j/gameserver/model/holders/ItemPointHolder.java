// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.StatsSet;

public class ItemPointHolder extends ItemHolder
{
    private final int _points;
    
    public ItemPointHolder(final StatsSet params) {
        this(params.getInt("id"), params.getLong("count"), params.getInt("points"));
    }
    
    public ItemPointHolder(final int id, final long count, final int points) {
        super(id, count);
        this._points = points;
    }
    
    public int getPoints() {
        return this._points;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IJI)Ljava/lang/String;, this.getClass().getSimpleName(), this.getId(), this.getCount(), this._points);
    }
}
