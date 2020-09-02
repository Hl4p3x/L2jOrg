// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerAgathionId extends Condition
{
    private final int _agathionId;
    
    public ConditionPlayerAgathionId(final int agathionId) {
        this._agathionId = agathionId;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() != null && effector.getActingPlayer().getAgathionId() == this._agathionId;
    }
}
