// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CanUntransformSkillCondition implements SkillCondition
{
    private CanUntransformSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        boolean canUntransform = true;
        final Player player = caster.getActingPlayer();
        if (player == null) {
            canUntransform = false;
        }
        else if (player.isAlikeDead()) {
            canUntransform = false;
        }
        else if (player.isFlyingMounted() && !player.isInsideZone(ZoneType.LANDING)) {
            player.sendPacket(SystemMessageId.YOU_ARE_TOO_HIGH_TO_PERFORM_THIS_ACTION_PLEASE_LOWER_YOUR_ALTITUDE_AND_TRY_AGAIN);
            canUntransform = false;
        }
        return canUntransform;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final CanUntransformSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "CanUntransform";
        }
        
        static {
            INSTANCE = new CanUntransformSkillCondition();
        }
    }
}
