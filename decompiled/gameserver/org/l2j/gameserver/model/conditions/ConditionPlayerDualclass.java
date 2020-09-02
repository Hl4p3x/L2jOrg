// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerDualclass extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerDualclass(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() == null || effector.getActingPlayer().isDualClassActive() == this._val;
    }
}
