// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers;

import org.l2j.gameserver.world.zone.ZoneRegion;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.handler.ITargetTypeHandler;

public class Ground implements ITargetTypeHandler
{
    public Enum<TargetType> getTargetType() {
        return (Enum<TargetType>)TargetType.GROUND;
    }
    
    public WorldObject getTarget(final Creature activeChar, final WorldObject selectedTarget, final Skill skill, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        if (GameUtils.isPlayer((WorldObject)activeChar)) {
            final Location worldPosition = activeChar.getActingPlayer().getCurrentSkillWorldPosition();
            if (worldPosition != null) {
                if (dontMove && !MathUtil.isInsideRadius2D((ILocational)activeChar, worldPosition.getX(), worldPosition.getY(), skill.getCastRange() + activeChar.getTemplate().getCollisionRadius())) {
                    return null;
                }
                if (!GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, worldPosition)) {
                    if (sendMessage) {
                        activeChar.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
                    }
                    return null;
                }
                final ZoneRegion zoneRegion = ZoneManager.getInstance().getRegion((ILocational)activeChar);
                if (skill.isBad() && !zoneRegion.checkEffectRangeInsidePeaceZone(skill, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) {
                    if (sendMessage) {
                        activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_SKILLS_THAT_MAY_HARM_OTHER_PLAYERS_IN_HERE);
                    }
                    return null;
                }
                return (WorldObject)activeChar;
            }
        }
        return null;
    }
}
