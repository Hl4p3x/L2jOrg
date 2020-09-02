// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerTransformationId extends Condition
{
    private final int _id;
    
    public ConditionPlayerTransformationId(final int id) {
        this._id = id;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return (this._id == -1) ? effector.isTransformed() : (effector.getTransformationId() == this._id);
    }
}
