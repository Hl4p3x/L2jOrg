// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.combination;

import java.util.EnumMap;
import org.l2j.gameserver.model.StatsSet;
import java.util.Map;

public class CombinationItem
{
    private final int _itemOne;
    private final int _itemTwo;
    private final int _chance;
    private final Map<CombinationItemType, CombinationItemReward> _rewards;
    
    public CombinationItem(final StatsSet set) {
        this._rewards = new EnumMap<CombinationItemType, CombinationItemReward>(CombinationItemType.class);
        this._itemOne = set.getInt("one");
        this._itemTwo = set.getInt("two");
        this._chance = set.getInt("chance");
    }
    
    public int getItemOne() {
        return this._itemOne;
    }
    
    public int getItemTwo() {
        return this._itemTwo;
    }
    
    public int getChance() {
        return this._chance;
    }
    
    public void addReward(final CombinationItemReward item) {
        this._rewards.put(item.getType(), item);
    }
    
    public CombinationItemReward getReward(final CombinationItemType type) {
        return this._rewards.get(type);
    }
}
