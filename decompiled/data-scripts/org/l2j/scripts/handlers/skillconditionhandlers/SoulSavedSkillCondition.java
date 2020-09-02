// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class SoulSavedSkillCondition implements SkillCondition
{
    public final int amount;
    
    private SoulSavedSkillCondition(final int amount) {
        this.amount = amount;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return GameUtils.isPlayer((WorldObject)caster) && caster.getActingPlayer().getChargedSouls() >= this.amount;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)new SoulSavedSkillCondition(this.parseInt(xmlNode.getAttributes(), "amount"));
        }
        
        public String conditionName() {
            return "soul";
        }
    }
}
