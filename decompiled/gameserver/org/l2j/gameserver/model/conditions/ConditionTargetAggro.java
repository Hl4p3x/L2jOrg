// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetAggro extends Condition
{
    private final boolean _isAggro;
    
    public ConditionTargetAggro(final boolean isAggro) {
        this._isAggro = isAggro;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (GameUtils.isMonster(effected)) {
            return ((Monster)effected).isAggressive() == this._isAggro;
        }
        return GameUtils.isPlayer(effected) && effected.getReputation() < 0;
    }
}
