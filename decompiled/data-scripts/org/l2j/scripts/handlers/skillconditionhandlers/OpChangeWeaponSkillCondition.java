// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpChangeWeaponSkillCondition implements SkillCondition
{
    private OpChangeWeaponSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final Weapon weaponItem = caster.getActiveWeaponItem();
        return weaponItem != null && weaponItem.getChangeWeaponId() != 0 && !caster.getActingPlayer().hasItemRequest();
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final OpChangeWeaponSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "OpChangeWeapon";
        }
        
        static {
            INSTANCE = new OpChangeWeaponSkillCondition();
        }
    }
}
