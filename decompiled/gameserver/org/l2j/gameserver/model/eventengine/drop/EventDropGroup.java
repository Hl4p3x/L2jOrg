// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine.drop;

import java.util.ArrayList;
import java.util.List;

public class EventDropGroup
{
    private final List<EventDropItem> _items;
    private final double _chance;
    
    public EventDropGroup(final double chance) {
        this._items = new ArrayList<EventDropItem>();
        this._chance = chance;
    }
    
    public double getChance() {
        return this._chance;
    }
    
    public List<EventDropItem> getItems() {
        return this._items;
    }
    
    public void addItem(final EventDropItem item) {
        this._items.add(item);
    }
}
