// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpEnergyMaxSkillCondition implements SkillCondition
{
    public final int charges;
    
    private OpEnergyMaxSkillCondition(final int charges) {
        this.charges = charges;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (caster.getActingPlayer().getCharges() >= this.charges) {
            caster.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
            return false;
        }
        return true;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)new OpEnergyMaxSkillCondition(this.parseInt(xmlNode.getAttributes(), "charges"));
        }
        
        public String conditionName() {
            return "energy-max";
        }
    }
}
