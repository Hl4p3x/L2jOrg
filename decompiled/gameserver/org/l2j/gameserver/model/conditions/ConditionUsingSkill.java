// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public final class ConditionUsingSkill extends Condition
{
    private final int _skillId;
    
    public ConditionUsingSkill(final int skillId) {
        this._skillId = skillId;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return skill != null && skill.getId() == this._skillId;
    }
}
