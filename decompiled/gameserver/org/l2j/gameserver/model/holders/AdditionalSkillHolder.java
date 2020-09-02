// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public class AdditionalSkillHolder extends SkillHolder
{
    private final int _minLevel;
    
    public AdditionalSkillHolder(final int skillId, final int skillLevel, final int minLevel) {
        super(skillId, skillLevel);
        this._minLevel = minLevel;
    }
    
    public int getMinLevel() {
        return this._minLevel;
    }
}
