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

public class GroupedDrop implements IEventDrop
{
    private final List<EventDropGroup> _groups;
    
    public GroupedDrop() {
        this._groups = new ArrayList<EventDropGroup>();
    }
    
    public List<EventDropGroup> getGroups() {
        return this._groups;
    }
    
    public void addGroup(final EventDropGroup group) {
        this._groups.add(group);
    }
    
    @Override
    public Collection<ItemHolder> calculateDrops() {
        final List<ItemHolder> rewards = new ArrayList<ItemHolder>();
        for (final EventDropGroup group : this._groups) {
            if (Rnd.nextDouble() * 100.0 < group.getChance()) {
                double totalChance = 0.0;
                final double random = Rnd.nextDouble() * 100.0;
                for (final EventDropItem item : group.getItems()) {
                    totalChance += item.getChance();
                    if (totalChance > random) {
                        final long count = Rnd.get(item.getMin(), item.getMax());
                        if (count > 0L) {
                            rewards.add(new ItemHolder(item.getId(), count));
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        return rewards;
    }
}
