// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class ConsumeBodySkillCondition implements SkillCondition
{
    private ConsumeBodySkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (GameUtils.isMonster(target) || GameUtils.isSummon(target)) {
            final Creature character = (Creature)target;
            if (character.isDead() && character.isSpawned()) {
                return true;
            }
        }
        if (GameUtils.isPlayer((WorldObject)caster)) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
        }
        return false;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final ConsumeBodySkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "ConsumeBody";
        }
        
        static {
            INSTANCE = new ConsumeBodySkillCondition();
        }
    }
}
