// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerHp extends Condition
{
    private final int _hp;
    
    public ConditionPlayerHp(final int hp) {
        this._hp = hp;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector != null && effector.getCurrentHp() * 100.0 / effector.getMaxHp() <= this._hp;
    }
}
