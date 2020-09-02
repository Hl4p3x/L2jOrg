// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CanSummonSkillCondition implements SkillCondition
{
    private CanSummonSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final Player player = caster.getActingPlayer();
        if (player == null || player.isSpawnProtected() || player.isTeleportProtected()) {
            return false;
        }
        boolean canSummon = true;
        if (player.isFlyingMounted() || player.isMounted() || player.inObserverMode() || player.isTeleporting()) {
            canSummon = false;
        }
        return canSummon;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final CanSummonSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "CanSummon";
        }
        
        static {
            INSTANCE = new CanSummonSkillCondition();
        }
    }
}
