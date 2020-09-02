// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.handler.ITargetTypeHandler;

public class Target implements ITargetTypeHandler
{
    public Enum<TargetType> getTargetType() {
        return (Enum<TargetType>)TargetType.TARGET;
    }
    
    public WorldObject getTarget(final Creature activeChar, final WorldObject selectedTarget, final Skill skill, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        if (selectedTarget == null) {
            return null;
        }
        if (!GameUtils.isCreature(selectedTarget)) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
            return null;
        }
        final Creature target = (Creature)selectedTarget;
        if (activeChar == target) {
            return (WorldObject)target;
        }
        if (dontMove && !MathUtil.isInsideRadius2D((ILocational)activeChar, (ILocational)target, skill.getCastRange())) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            }
            return null;
        }
        if (skill.isFlyType() && !GeoEngine.getInstance().canMoveToTarget(activeChar.getX(), activeChar.getY(), activeChar.getZ(), target.getX(), target.getY(), target.getZ(), activeChar.getInstanceWorld())) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE);
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
