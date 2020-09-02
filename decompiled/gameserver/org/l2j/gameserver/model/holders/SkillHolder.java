// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.skill.api.Skill;

public class SkillHolder
{
    private final int _skillId;
    private final int _skillLevel;
    private final int _skillSubLevel;
    private Skill skill;
    
    public SkillHolder(final int skillId, final int skillLevel) {
        this._skillId = skillId;
        this._skillLevel = skillLevel;
        this._skillSubLevel = 0;
    }
    
    public SkillHolder(final int skillId, final int skillLevel, final int skillSubLevel) {
        this._skillId = skillId;
        this._skillLevel = skillLevel;
        this._skillSubLevel = skillSubLevel;
    }
    
    public SkillHolder(final Skill skill) {
        this._skillId = skill.getId();
        this._skillLevel = skill.getLevel();
        this._skillSubLevel = skill.getSubLevel();
    }
    
    public final int getSkillId() {
        return this._skillId;
    }
    
    public final int getLevel() {
        return this._skillLevel;
    }
    
    public final int getSkillSubLevel() {
        return this._skillSubLevel;
    }
    
    public final int getMaxLevel() {
        return SkillEngine.getInstance().getMaxLevel(this._skillId);
    }
    
    public final Skill getSkill() {
        if (Objects.isNull(this.skill)) {
            this.skill = SkillEngine.getInstance().getSkill(this._skillId, Math.max(this._skillLevel, 1));
        }
        return this.skill;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SkillHolder)) {
            return false;
        }
        final SkillHolder holder = (SkillHolder)obj;
        return holder.getSkillId() == this._skillId && holder.getLevel() == this._skillLevel && holder.getSkillSubLevel() == this._skillSubLevel;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this._skillId;
        result = 31 * result + this._skillLevel;
        result = 31 * result + this._skillSubLevel;
        return result;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this._skillId, this._skillLevel);
    }
}
