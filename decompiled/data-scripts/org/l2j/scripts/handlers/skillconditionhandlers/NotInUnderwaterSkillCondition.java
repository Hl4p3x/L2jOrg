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

public class NotInUnderwaterSkillCondition implements SkillCondition
{
    private NotInUnderwaterSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return !caster.isInsideZone(ZoneType.WATER);
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final NotInUnderwaterSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "NotInUnderwater";
        }
        
        static {
            INSTANCE = new NotInUnderwaterSkillCondition();
        }
    }
}
