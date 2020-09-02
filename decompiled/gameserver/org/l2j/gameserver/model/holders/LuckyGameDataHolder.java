// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import java.util.List;

public class LuckyGameDataHolder
{
    private final int _index;
    private final int _turningPoints;
    private final List<ItemChanceHolder> _commonRewards;
    private final List<ItemPointHolder> _uniqueRewards;
    private final List<ItemChanceHolder> _modifyRewards;
    private int _minModifyRewardGame;
    private int _maxModifyRewardGame;
    
    public LuckyGameDataHolder(final StatsSet params) {
        this._commonRewards = new ArrayList<ItemChanceHolder>();
        this._uniqueRewards = new ArrayList<ItemPointHolder>();
        this._modifyRewards = new ArrayList<ItemChanceHolder>();
        this._index = params.getInt("index");
        this._turningPoints = params.getInt("turning_point");
    }
    
    public void addCommonReward(final ItemChanceHolder item) {
        this._commonRewards.add(item);
    }
    
    public void addUniqueReward(final ItemPointHolder item) {
        this._uniqueRewards.add(item);
    }
    
    public void addModifyReward(final ItemChanceHolder item) {
        this._modifyRewards.add(item);
    }
    
    public List<ItemChanceHolder> getCommonReward() {
        return this._commonRewards;
    }
    
    public List<ItemPointHolder> getUniqueReward() {
        return this._uniqueRewards;
    }
    
    public List<ItemChanceHolder> getModifyReward() {
        return this._modifyRewards;
    }
    
    public int getMinModifyRewardGame() {
        return this._minModifyRewardGame;
    }
    
    public void setMinModifyRewardGame(final int minModifyRewardGame) {
        this._minModifyRewardGame = minModifyRewardGame;
    }
    
    public int getMaxModifyRewardGame() {
        return this._maxModifyRewardGame;
    }
    
    public void setMaxModifyRewardGame(final int maxModifyRewardGame) {
        this._maxModifyRewardGame = maxModifyRewardGame;
    }
    
    public int getIndex() {
        return this._index;
    }
    
    public int getTurningPoints() {
        return this._turningPoints;
    }
}
