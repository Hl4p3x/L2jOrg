// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CanUseInBattlefieldSkillCondition implements SkillCondition
{
    private CanUseInBattlefieldSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return caster != null && caster.isInsideZone(ZoneType.SIEGE);
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final CanUseInBattlefieldSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "CanUseInBattlefield";
        }
        
        static {
            INSTANCE = new CanUseInBattlefieldSkillCondition();
        }
    }
}
