// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import java.util.Objects;
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

public class BuildCampSkillCondition implements SkillCondition
{
    private BuildCampSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isPlayer((WorldObject)caster)) {
            return false;
        }
        final Player player = caster.getActingPlayer();
        boolean canCreateBase = true;
        if (player.isAlikeDead() || player.getClan() == null) {
            canCreateBase = false;
        }
        final Castle castle = CastleManager.getInstance().getCastle((ILocational)player);
        if (castle == null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            canCreateBase = false;
        }
        else if (!castle.getSiege().isInProgress()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            canCreateBase = false;
        }
        else if (Objects.isNull(castle.getSiege().getAttackerClan(player.getClan()))) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            canCreateBase = false;
        }
        else if (!player.isClanLeader()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            canCreateBase = false;
        }
        else if (Util.zeroIfNullOrElse((Object)castle.getSiege().getAttackerClan(player.getClan()), SiegeClanData::getNumFlags) >= SiegeManager.getInstance().getFlagMaxCount()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            canCreateBase = false;
        }
        else if (!player.isInsideZone(ZoneType.HQ)) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_BUILD_HEADQUARTERS_HERE);
            canCreateBase = false;
        }
        return canCreateBase;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final BuildCampSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "BuildCamp";
        }
        
        static {
            INSTANCE = new BuildCampSkillCondition();
        }
    }
}
