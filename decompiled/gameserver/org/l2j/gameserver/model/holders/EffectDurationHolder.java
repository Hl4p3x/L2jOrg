// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.engine.skill.api.Skill;

public class EffectDurationHolder
{
    private final int _skillId;
    private final int _skillLvl;
    private final int _duration;
    
    public EffectDurationHolder(final Skill skill, final int duration) {
        this._skillId = skill.getDisplayId();
        this._skillLvl = skill.getDisplayLevel();
        this._duration = duration;
    }
    
    public int getSkillId() {
        return this._skillId;
    }
    
    public int getSkillLvl() {
        return this._skillLvl;
    }
    
    public int getDuration() {
        return this._duration;
    }
}
