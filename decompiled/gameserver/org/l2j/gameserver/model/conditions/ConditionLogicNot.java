// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionLogicNot extends Condition
{
    public final Condition _condition;
    
    public ConditionLogicNot(final Condition condition) {
        this._condition = condition;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return !this._condition.test(effector, effected, skill, item);
    }
}
