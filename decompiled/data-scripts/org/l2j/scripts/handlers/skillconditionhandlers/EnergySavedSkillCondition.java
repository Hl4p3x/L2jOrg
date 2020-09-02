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

public class EnergySavedSkillCondition implements SkillCondition
{
    public final int charges;
    
    private EnergySavedSkillCondition(final int charges) {
        this.charges = charges;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return caster.getActingPlayer().getCharges() >= this.charges;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)new EnergySavedSkillCondition(this.parseInt(xmlNode.getAttributes(), "charges"));
        }
        
        public String conditionName() {
            return "energy-saved";
        }
    }
}
