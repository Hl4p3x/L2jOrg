// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpSoulMaxSkillCondition implements SkillCondition
{
    private OpSoulMaxSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final int maxSouls = (int)caster.getStats().getValue(Stat.MAX_SOULS);
        return GameUtils.isPlayable((WorldObject)caster) && caster.getActingPlayer().getChargedSouls() < maxSouls;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final OpSoulMaxSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "OpSoulMax";
        }
        
        static {
            INSTANCE = new OpSoulMaxSkillCondition();
        }
    }
}
