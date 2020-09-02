// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.Collections;
import java.util.List;

public class CrystallizationDataHolder
{
    private final int _id;
    private final List<ItemChanceHolder> _items;
    
    public CrystallizationDataHolder(final int id, final List<ItemChanceHolder> items) {
        this._id = id;
        this._items = Collections.unmodifiableList((List<? extends ItemChanceHolder>)items);
    }
    
    public int getId() {
        return this._id;
    }
    
    public List<ItemChanceHolder> getItems() {
        return Collections.unmodifiableList((List<? extends ItemChanceHolder>)this._items);
    }
}
