// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.holders.RestorationItemHolder;
import java.util.List;

public class ExtractableProductItem
{
    private final List<RestorationItemHolder> _items;
    private final double _chance;
    
    public ExtractableProductItem(final List<RestorationItemHolder> items, final double chance) {
        this._items = items;
        this._chance = chance;
    }
    
    public List<RestorationItemHolder> getItems() {
        return this._items;
    }
    
    public double getChance() {
        return this._chance;
    }
}
