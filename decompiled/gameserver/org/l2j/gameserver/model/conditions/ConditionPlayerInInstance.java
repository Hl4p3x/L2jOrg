// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerInInstance extends Condition
{
    public final boolean _inInstance;
    
    public ConditionPlayerInInstance(final boolean inInstance) {
        this._inInstance = inInstance;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() != null && ((effector.getInstanceId() == 0) ? (!this._inInstance) : this._inInstance);
    }
}
