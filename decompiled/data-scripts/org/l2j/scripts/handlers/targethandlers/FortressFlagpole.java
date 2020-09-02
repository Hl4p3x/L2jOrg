// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.handler.ITargetTypeHandler;

public class FortressFlagpole implements ITargetTypeHandler
{
    public Enum<TargetType> getTargetType() {
        return (Enum<TargetType>)TargetType.FORTRESS_FLAGPOLE;
    }
    
    public WorldObject getTarget(final Creature activeChar, final WorldObject selectedTarget, final Skill skill, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        final WorldObject target = activeChar.getTarget();
        if (target != null && activeChar.isInsideZone(ZoneType.HQ) && activeChar.isInsideZone(ZoneType.FORT) && !GameUtils.isPlayable(target) && target.getName().toLowerCase().contains("flagpole")) {
            return target;
        }
        if (sendMessage) {
            activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
        }
        return null;
    }
}
