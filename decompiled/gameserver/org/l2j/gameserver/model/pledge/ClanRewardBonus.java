// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.pledge;

import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.enums.ClanRewardType;

public class ClanRewardBonus
{
    private final ClanRewardType _type;
    private final int _level;
    private final int _requiredAmount;
    private SkillHolder _skillReward;
    private ItemHolder _itemReward;
    
    public ClanRewardBonus(final ClanRewardType type, final int level, final int requiredAmount) {
        this._type = type;
        this._level = level;
        this._requiredAmount = requiredAmount;
    }
    
    public ClanRewardType getType() {
        return this._type;
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public int getRequiredAmount() {
        return this._requiredAmount;
    }
    
    public SkillHolder getSkillReward() {
        return this._skillReward;
    }
    
    public void setSkillReward(final SkillHolder skillReward) {
        this._skillReward = skillReward;
    }
    
    public ItemHolder getItemReward() {
        return this._itemReward;
    }
    
    public void setItemReward(final ItemHolder itemReward) {
        this._itemReward = itemReward;
    }
}
