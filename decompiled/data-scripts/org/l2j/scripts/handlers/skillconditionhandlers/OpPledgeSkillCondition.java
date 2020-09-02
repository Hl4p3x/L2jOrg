// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.Clan;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpPledgeSkillCondition implements SkillCondition
{
    public final int level;
    
    private OpPledgeSkillCondition(final int level) {
        this.level = level;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final Clan clan = caster.getClan();
        return Objects.nonNull(clan) && clan.getLevel() >= this.level;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)new OpPledgeSkillCondition(this.parseInt(xmlNode.getAttributes(), "level"));
        }
        
        public String conditionName() {
            return "clan";
        }
    }
}
