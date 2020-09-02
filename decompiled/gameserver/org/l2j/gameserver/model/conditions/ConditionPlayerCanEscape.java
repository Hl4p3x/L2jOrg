// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanEscape extends Condition
{
    private final boolean val;
    
    public ConditionPlayerCanEscape(final boolean val) {
        this.val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return this.val == GameUtils.canTeleport(effector.getActingPlayer());
    }
}
