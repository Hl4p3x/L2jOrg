// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpCallPcSkillCondition implements SkillCondition
{
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        boolean canCallPlayer = true;
        final Player player = caster.getActingPlayer();
        if (player == null) {
            canCallPlayer = false;
        }
        else if (player.isInOlympiadMode()) {
            player.sendPacket(SystemMessageId.A_USER_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_USE_SUMMONING_OR_TELEPORTING);
            canCallPlayer = false;
        }
        else if (player.inObserverMode()) {
            canCallPlayer = false;
        }
        else if (player.isInsideZone(ZoneType.NO_SUMMON_FRIEND) || player.isInsideZone(ZoneType.JAIL) || player.isFlyingMounted()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_SUMMONING_OR_TELEPORTING_IN_THIS_AREA);
            canCallPlayer = false;
        }
        return canCallPlayer;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final OpCallPcSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "OpCallPc";
        }
        
        static {
            INSTANCE = new OpCallPcSkillCondition();
        }
    }
}
