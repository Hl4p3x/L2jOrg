// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.ArrayList;
import java.util.List;

public class FishingBaitData
{
    private final int _itemId;
    private final int _level;
    private final int _minPlayerLevel;
    private final double _chance;
    private final int _timeMin;
    private final int _timeMax;
    private final int _waitMin;
    private final int _waitMax;
    private final List<Integer> _rewards;
    
    public FishingBaitData(final int itemId, final int level, final int minPlayerLevel, final double chance, final int timeMin, final int timeMax, final int waitMin, final int waitMax) {
        this._rewards = new ArrayList<Integer>();
        this._itemId = itemId;
        this._level = level;
        this._minPlayerLevel = minPlayerLevel;
        this._chance = chance;
        this._timeMin = timeMin;
        this._timeMax = timeMax;
        this._waitMin = waitMin;
        this._waitMax = waitMax;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public int getMinPlayerLevel() {
        return this._minPlayerLevel;
    }
    
    public double getChance() {
        return this._chance;
    }
    
    public int getTimeMin() {
        return this._timeMin;
    }
    
    public int getTimeMax() {
        return this._timeMax;
    }
    
    public int getWaitMin() {
        return this._waitMin;
    }
    
    public int getWaitMax() {
        return this._waitMax;
    }
    
    public List<Integer> getRewards() {
        return this._rewards;
    }
    
    public void addReward(final int itemId) {
        this._rewards.add(itemId);
    }
}
