// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpResurrectionSkillCondition implements SkillCondition
{
    private OpResurrectionSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        boolean canResurrect = true;
        if (target == caster) {
            return canResurrect;
        }
        if (target == null) {
            return false;
        }
        if (GameUtils.isPlayer(target)) {
            final Player player = target.getActingPlayer();
            if (!player.isDead()) {
                canResurrect = false;
                if (GameUtils.isPlayer((WorldObject)caster)) {
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
                    msg.addSkillName(skill);
                    caster.sendPacket(new ServerPacket[] { (ServerPacket)msg });
                }
            }
            else if (player.isResurrectionBlocked()) {
                canResurrect = false;
                if (GameUtils.isPlayer((WorldObject)caster)) {
                    caster.sendPacket(SystemMessageId.REJECT_RESURRECTION);
                }
            }
            else if (player.isReviveRequested()) {
                canResurrect = false;
                if (GameUtils.isPlayer((WorldObject)caster)) {
                    caster.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                }
            }
        }
        else if (GameUtils.isSummon(target)) {
            final Summon summon = (Summon)target;
            final Player player2 = target.getActingPlayer();
            if (!summon.isDead()) {
                canResurrect = false;
                if (GameUtils.isPlayer((WorldObject)caster)) {
                    final SystemMessage msg2 = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
                    msg2.addSkillName(skill);
                    caster.sendPacket(new ServerPacket[] { (ServerPacket)msg2 });
                }
            }
            else if (summon.isResurrectionBlocked()) {
                canResurrect = false;
                if (GameUtils.isPlayer((WorldObject)caster)) {
                    caster.sendPacket(SystemMessageId.REJECT_RESURRECTION);
                }
            }
            else if (player2 != null && player2.isRevivingPet()) {
                canResurrect = false;
                if (GameUtils.isPlayer((WorldObject)caster)) {
                    caster.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                }
            }
        }
        return canResurrect;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final OpResurrectionSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "OpResurrection";
        }
        
        static {
            INSTANCE = new OpResurrectionSkillCondition();
        }
    }
}
