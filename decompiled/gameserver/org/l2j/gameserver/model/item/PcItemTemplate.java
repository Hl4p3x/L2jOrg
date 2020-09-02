// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.ItemHolder;

public final class PcItemTemplate extends ItemHolder
{
    private final boolean _equipped;
    
    public PcItemTemplate(final StatsSet set) {
        super(set.getInt("id"), set.getInt("count"));
        this._equipped = set.getBoolean("equipped", false);
    }
    
    public boolean isEquipped() {
        return this._equipped;
    }
}
