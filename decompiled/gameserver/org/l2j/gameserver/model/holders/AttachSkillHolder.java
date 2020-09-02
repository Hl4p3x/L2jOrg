// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.StatsSet;

public class AttachSkillHolder extends SkillHolder
{
    private final int _requiredSkillId;
    private final int _requiredSkillLevel;
    
    public AttachSkillHolder(final int skillId, final int skillLevel, final int requiredSkillId, final int requiredSkillLevel) {
        super(skillId, skillLevel);
        this._requiredSkillId = requiredSkillId;
        this._requiredSkillLevel = requiredSkillLevel;
    }
    
    public static AttachSkillHolder fromStatsSet(final StatsSet set) {
        return new AttachSkillHolder(set.getInt("skillId"), set.getInt("skillLevel", 1), set.getInt("requiredSkillId"), set.getInt("requiredSkillLevel", 1));
    }
    
    public int getRequiredSkillId() {
        return this._requiredSkillId;
    }
    
    public int getRequiredSkillLevel() {
        return this._requiredSkillLevel;
    }
}
