// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.stream.Stream;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.function.Function;
import java.util.ArrayList;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.item.type.WeaponType;
import java.util.List;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class Op2hWeaponSkillCondition implements SkillCondition
{
    public final List<WeaponType> _weaponTypes;
    
    public Op2hWeaponSkillCondition(final StatsSet params) {
        this._weaponTypes = new ArrayList<WeaponType>();
        final List<String> weaponTypes = (List<String>)params.getList("weaponType", (Class)String.class);
        if (weaponTypes != null) {
            final Stream<Object> map = weaponTypes.stream().map((Function<? super Object, ?>)WeaponType::valueOf);
            final List<WeaponType> weaponTypes2 = this._weaponTypes;
            Objects.requireNonNull(weaponTypes2);
            map.forEach(weaponTypes2::add);
        }
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final Weapon weapon = caster.getActiveWeaponItem();
        final Weapon weapon2;
        final boolean b;
        return weapon != null && this._weaponTypes.stream().anyMatch(weaponType -> {
            if (weapon2.getItemType() == weaponType) {
                if (weapon2.getBodyPart().isAnyOf(new BodyPart[] { BodyPart.TWO_HAND, BodyPart.RIGHT_HAND })) {
                    return b;
                }
            }
            return b;
        });
    }
}
