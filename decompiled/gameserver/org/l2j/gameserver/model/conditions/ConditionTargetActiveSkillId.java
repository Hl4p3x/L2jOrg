// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetActiveSkillId extends Condition
{
    private final int _skillId;
    private final int _skillLevel;
    
    public ConditionTargetActiveSkillId(final int skillId) {
        this._skillId = skillId;
        this._skillLevel = -1;
    }
    
    public ConditionTargetActiveSkillId(final int skillId, final int skillLevel) {
        this._skillId = skillId;
        this._skillLevel = skillLevel;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Skill knownSkill = effected.getKnownSkill(this._skillId);
        return knownSkill != null && (this._skillLevel == -1 || this._skillLevel <= knownSkill.getLevel());
    }
}
