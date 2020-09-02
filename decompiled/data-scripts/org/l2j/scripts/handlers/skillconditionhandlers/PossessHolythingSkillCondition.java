// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class PossessHolythingSkillCondition implements SkillCondition
{
    private PossessHolythingSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isPlayer((WorldObject)caster)) {
            return false;
        }
        final Player player = caster.getActingPlayer();
        boolean canTakeCastle = true;
        if (player.isAlikeDead() || !player.isClanLeader()) {
            canTakeCastle = false;
        }
        final Castle castle = CastleManager.getInstance().getCastle((ILocational)player);
        if (castle == null || castle.getId() <= 0 || !castle.getSiege().isInProgress() || castle.getSiege().getAttackerClan(player.getClan()) == null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            canTakeCastle = false;
        }
        else if (!castle.getArtefacts().contains(target)) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canTakeCastle = false;
        }
        else if (!GameUtils.checkIfInRange(skill.getCastRange(), (WorldObject)player, target, true) || Math.abs(player.getZ() - target.getZ()) > 40) {
            player.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            canTakeCastle = false;
        }
        return canTakeCastle;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final PossessHolythingSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "PossessHolything";
        }
        
        static {
            INSTANCE = new PossessHolythingSkillCondition();
        }
    }
}
