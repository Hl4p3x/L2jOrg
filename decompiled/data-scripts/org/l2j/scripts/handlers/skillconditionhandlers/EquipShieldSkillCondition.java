// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class EquipShieldSkillCondition implements SkillCondition
{
    private EquipShieldSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final ItemTemplate shield = caster.getSecondaryWeaponItem();
        return shield != null && shield.getItemType() == ArmorType.SHIELD;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final EquipShieldSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "EquipShield";
        }
        
        static {
            INSTANCE = new EquipShieldSkillCondition();
        }
    }
}
