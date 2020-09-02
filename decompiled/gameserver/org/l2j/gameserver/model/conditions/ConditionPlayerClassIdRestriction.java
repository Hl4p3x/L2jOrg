// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.List;

public class ConditionPlayerClassIdRestriction extends Condition
{
    private final List<Integer> _classIds;
    
    public ConditionPlayerClassIdRestriction(final List<Integer> classId) {
        this._classIds = classId;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() != null && this._classIds.contains(effector.getActingPlayer().getClassId().getId());
    }
}
