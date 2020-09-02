// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetUsesWeaponKind extends Condition
{
    private final int _weaponMask;
    
    public ConditionTargetUsesWeaponKind(final int weaponMask) {
        this._weaponMask = weaponMask;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (effected == null) {
            return false;
        }
        final Weapon weapon = effected.getActiveWeaponItem();
        return weapon != null && (weapon.getItemType().mask() & this._weaponMask) != 0x0;
    }
}
