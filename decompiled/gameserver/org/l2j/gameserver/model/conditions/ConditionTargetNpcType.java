// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;

public class ConditionTargetNpcType extends Condition
{
    private final InstanceType[] _npcType;
    
    public ConditionTargetNpcType(final InstanceType[] type) {
        this._npcType = type;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effected != null && effected.getInstanceType().isTypes(this._npcType);
    }
}
