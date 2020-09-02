// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionGameTime extends Condition
{
    private final CheckGameTime _check;
    private final boolean _required;
    
    public ConditionGameTime(final CheckGameTime check, final boolean required) {
        this._check = check;
        this._required = required;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        switch (this._check) {
            case NIGHT: {
                return WorldTimeController.getInstance().isNight() == this._required;
            }
            default: {
                return !this._required;
            }
        }
    }
    
    public enum CheckGameTime
    {
        NIGHT;
    }
}
