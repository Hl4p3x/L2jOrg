// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers;

import org.l2j.gameserver.model.Mentee;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.handler.ITargetTypeHandler;

public class MyMentor implements ITargetTypeHandler
{
    public Enum<TargetType> getTargetType() {
        return (Enum<TargetType>)TargetType.MY_MENTOR;
    }
    
    public WorldObject getTarget(final Creature activeChar, final WorldObject selectedTarget, final Skill skill, final boolean forceUse, final boolean dontMove, final boolean sendMessage) {
        if (GameUtils.isPlayer((WorldObject)activeChar)) {
            final Mentee mentor = MentorManager.getInstance().getMentor(activeChar.getObjectId());
            if (mentor != null) {
                return (WorldObject)mentor.getPlayerInstance();
            }
        }
        return null;
    }
}
