// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionChangeWeapon extends Condition
{
    private final boolean _required;
    
    public ConditionChangeWeapon(final boolean required) {
        this._required = required;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        if (this._required) {
            final Weapon weaponItem = effector.getActiveWeaponItem();
            if (weaponItem == null) {
                return false;
            }
            if (weaponItem.getChangeWeaponId() == 0) {
                return false;
            }
            if (effector.getActingPlayer().hasItemRequest()) {
                return false;
            }
        }
        return true;
    }
}
