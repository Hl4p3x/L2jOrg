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

public class NpcBody implements ITargetTypeHandler
{
    public Enum<TargetType> getTargetType() {
        return (Enum<TargetType>)TargetType.NPC_BODY;
    }
    
    public WorldObject getTarget(final Creature activeChar, final WorldObject selectedTarget, final Skill skill, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        if (!GameUtils.isCreature(selectedTarget)) {
            return null;
        }
        if (!GameUtils.isNpc(selectedTarget) && !GameUtils.isSummon(selectedTarget)) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
            return null;
        }
        final Creature cha = (Creature)selectedTarget;
        if (!cha.isDead()) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
            return null;
        }
        if (dontMove && !MathUtil.isInsideRadius2D((ILocational)activeChar, (ILocational)cha, skill.getCastRange())) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            }
            return null;
        }
        if (!GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, (WorldObject)cha)) {
            if (sendMessage) {
                activeChar.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
            }
            return null;
        }
        return (WorldObject)cha;
    }
}
