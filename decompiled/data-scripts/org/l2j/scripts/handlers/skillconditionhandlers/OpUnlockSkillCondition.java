// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpUnlockSkillCondition implements SkillCondition
{
    private OpUnlockSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return target != null && (GameUtils.isDoor(target) || target instanceof Chest);
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final OpUnlockSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "OpUnlock";
        }
        
        static {
            INSTANCE = new OpUnlockSkillCondition();
        }
    }
}
