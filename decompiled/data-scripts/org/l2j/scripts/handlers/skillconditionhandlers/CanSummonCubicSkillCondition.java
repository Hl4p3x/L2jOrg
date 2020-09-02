// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CanSummonCubicSkillCondition implements SkillCondition
{
    private CanSummonCubicSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isPlayer((WorldObject)caster) || caster.isAlikeDead() || caster.getActingPlayer().inObserverMode()) {
            return false;
        }
        final Player player = caster.getActingPlayer();
        return !player.inObserverMode() && !player.isMounted() && !player.isSpawnProtected() && !player.isTeleportProtected();
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final CanSummonCubicSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "CanSummonCubic";
        }
        
        static {
            INSTANCE = new CanSummonCubicSkillCondition();
        }
    }
}
