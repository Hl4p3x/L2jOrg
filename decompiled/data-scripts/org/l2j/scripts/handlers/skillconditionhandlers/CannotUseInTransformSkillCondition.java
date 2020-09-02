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

public class CannotUseInTransformSkillCondition implements SkillCondition
{
    public final int id;
    
    private CannotUseInTransformSkillCondition(final int id) {
        this.id = id;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return (this.id > 0) ? (caster.getTransformationId() != this.id) : (!caster.isTransformed());
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)new CannotUseInTransformSkillCondition(this.parseInt(xmlNode.getAttributes(), "id"));
        }
        
        public String conditionName() {
            return "non-transformed";
        }
    }
}
