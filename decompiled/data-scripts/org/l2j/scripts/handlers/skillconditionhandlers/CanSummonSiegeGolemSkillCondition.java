// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CanSummonSiegeGolemSkillCondition implements SkillCondition
{
    private CanSummonSiegeGolemSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isPlayer((WorldObject)caster)) {
            return false;
        }
        final Player player = caster.getActingPlayer();
        boolean canSummonSiegeGolem = true;
        if (player.isAlikeDead() || player.getClan() == null) {
            canSummonSiegeGolem = false;
        }
        final Castle castle = CastleManager.getInstance().getCastle((ILocational)player);
        if (Objects.isNull(castle)) {
            canSummonSiegeGolem = false;
        }
        if (castle != null && castle.getId() == 0) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        }
        else if (Objects.nonNull(castle) && !castle.getSiege().isInProgress()) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        }
        else if (player.getClanId() != 0 && Objects.nonNull(castle) && Objects.isNull(castle.getSiege().getAttackerClan(player.getClanId()))) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        }
        return canSummonSiegeGolem;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final CanSummonSiegeGolemSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "CanSummonSiegeGolem";
        }
        
        static {
            INSTANCE = new CanSummonSiegeGolemSkillCondition();
        }
    }
}
