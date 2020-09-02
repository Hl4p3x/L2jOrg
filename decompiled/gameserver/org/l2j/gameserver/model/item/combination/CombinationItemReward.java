// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.combination;

import org.l2j.gameserver.model.holders.ItemHolder;

public class CombinationItemReward extends ItemHolder
{
    private final CombinationItemType _type;
    
    public CombinationItemReward(final int id, final int count, final CombinationItemType type) {
        super(id, count);
        this._type = type;
    }
    
    public CombinationItemType getType() {
        return this._type;
    }
}
