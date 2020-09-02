// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class NonChaoticSkillCondition implements SkillCondition
{
    private NonChaoticSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return caster.getActingPlayer().getReputation() >= 0;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final NonChaoticSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "NonChaotic";
        }
        
        static {
            INSTANCE = new NonChaoticSkillCondition();
        }
    }
}
