// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine.drop;

import java.util.Iterator;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class NormalDrop implements IEventDrop
{
    private final List<EventDropItem> _items;
    
    public NormalDrop() {
        this._items = new ArrayList<EventDropItem>();
    }
    
    public List<EventDropItem> getItems() {
        return this._items;
    }
    
    public void addItem(final EventDropItem item) {
        this._items.add(item);
    }
    
    @Override
    public Collection<ItemHolder> calculateDrops() {
        final List<ItemHolder> rewards = new ArrayList<ItemHolder>();
        double totalChance = 0.0;
        final double random = Rnd.nextDouble() * 100.0;
        for (final EventDropItem item : this._items) {
            totalChance += item.getChance();
            if (totalChance > random) {
                final long count = Rnd.get(item.getMin(), item.getMax());
                if (count <= 0L) {
                    continue;
                }
                rewards.add(new ItemHolder(item.getId(), count));
            }
        }
        return rewards;
    }
}
