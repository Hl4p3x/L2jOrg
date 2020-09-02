// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.handler.ITargetTypeHandler;

public class PcBody implements ITargetTypeHandler
{
    public Enum<TargetType> getTargetType() {
        return (Enum<TargetType>)TargetType.PC_BODY;
    }
    
    public WorldObject getTarget(final Creature activeChar, final WorldObject selectedTarget, final Skill skill, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        if (selectedTarget == null) {
            return null;
        }
        if (!GameUtils.isCreature(selectedTarget)) {
            return null;
        }
        if (!GameUtils.isPlayer(selectedTarget) && !GameUtils.isPet(selectedTarget)) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
            return null;
        }
        final Playable target = (Playable)selectedTarget;
        if (!target.isDead()) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
            return null;
        }
        if (skill.hasAnyEffectType(new EffectType[] { EffectType.RESURRECTION })) {
            if (activeChar.isResurrectionBlocked() || target.isResurrectionBlocked()) {
                if (sendMessage) {
                    activeChar.sendPacket(SystemMessageId.REJECT_RESURRECTION);
                    target.sendPacket(SystemMessageId.REJECT_RESURRECTION);
                }
                return null;
            }
            if (GameUtils.isPlayer((WorldObject)target) && target.isInsideZone(ZoneType.SIEGE) && !target.getActingPlayer().isInSiege()) {
                if (sendMessage) {
                    activeChar.sendPacket(SystemMessageId.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEGROUNDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
                }
                return null;
            }
        }
        if (dontMove && !MathUtil.isInsideRadius2D((ILocational)activeChar, (ILocational)target, skill.getCastRange())) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            }
            return null;
        }
        if (!GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, (WorldObject)target)) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
            }
            return null;
        }
        return (WorldObject)target;
    }
}
