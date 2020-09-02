// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.model.item.type.WeaponType;
import java.util.Arrays;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class EquipWeaponSkillCondition implements SkillCondition
{
    public int mask;
    
    private EquipWeaponSkillCondition(final int mask) {
        this.mask = mask;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final ItemTemplate weapon = (ItemTemplate)caster.getActiveWeaponItem();
        return Objects.nonNull(weapon) && (weapon.getItemMask() & this.mask) != 0x0;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final int mask = Arrays.stream(xmlNode.getFirstChild().getTextContent().split(" ")).mapToInt(s -> WeaponType.valueOf(s).mask()).reduce(0, (a, b) -> a | b);
            return (SkillCondition)new EquipWeaponSkillCondition(mask);
        }
        
        public String conditionName() {
            return "weapon";
        }
    }
}
