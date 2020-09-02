// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionUsingSlotType extends Condition
{
    private final long _mask;
    
    public ConditionUsingSlotType(final long mask) {
        this._mask = mask;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return GameUtils.isPlayer(effector) && (effector.getActiveWeaponItem().getBodyPart().getId() & this._mask) != 0x0L;
    }
}
