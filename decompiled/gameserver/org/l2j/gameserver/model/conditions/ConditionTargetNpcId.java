// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.List;

public class ConditionTargetNpcId extends Condition
{
    private final List<Integer> _npcIds;
    
    public ConditionTargetNpcId(final List<Integer> npcIds) {
        this._npcIds = npcIds;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return (GameUtils.isNpc(effected) || GameUtils.isDoor(effected)) && this._npcIds.contains(effected.getId());
    }
}
