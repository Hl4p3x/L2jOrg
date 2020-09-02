// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpSkillAcquireSkillCondition implements SkillCondition
{
    public final int skillId;
    public final boolean hasLearned;
    
    private OpSkillAcquireSkillCondition(final int skill, final boolean learned) {
        this.skillId = skill;
        this.hasLearned = learned;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isCreature(target)) {
            return false;
        }
        final int skillLevel = ((Creature)target).getSkillLevel(this.skillId);
        return this.hasLearned == (skillLevel != 0);
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final NamedNodeMap attr = xmlNode.getAttributes();
            return (SkillCondition)new OpSkillAcquireSkillCondition(this.parseInt(attr, "id"), this.parseBoolean(attr, "learned"));
        }
        
        public String conditionName() {
            return "skill";
        }
    }
}
