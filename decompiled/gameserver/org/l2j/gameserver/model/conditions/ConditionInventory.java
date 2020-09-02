// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public abstract class ConditionInventory extends Condition
{
    protected final int _slot;
    
    public ConditionInventory(final int slot) {
        this._slot = slot;
    }
    
    @Override
    public abstract boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item);
}
