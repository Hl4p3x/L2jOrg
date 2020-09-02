// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.skill.api;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

public interface SkillCondition
{
    boolean canUse(final Creature caster, final Skill skill, final WorldObject target);
}
