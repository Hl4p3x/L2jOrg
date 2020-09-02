// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.handler.ITargetTypeHandler;

public class DoorTreasure implements ITargetTypeHandler
{
    public Enum<TargetType> getTargetType() {
        return (Enum<TargetType>)TargetType.DOOR_TREASURE;
    }
    
    public WorldObject getTarget(final Creature activeChar, final WorldObject selectedTarget, final Skill skill, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        final WorldObject target = activeChar.getTarget();
        if (target != null && (GameUtils.isDoor(target) || target instanceof Chest)) {
            return target;
        }
        if (sendMessage) {
            activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
        }
        return null;
    }
}
