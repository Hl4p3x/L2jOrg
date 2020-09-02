// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerLandingZone extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerLandingZone(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.isInsideZone(ZoneType.LANDING) == this._val;
    }
}
