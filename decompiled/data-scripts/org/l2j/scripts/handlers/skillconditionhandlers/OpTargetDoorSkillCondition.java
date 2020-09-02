// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpTargetDoorSkillCondition implements SkillCondition
{
    public final IntSet doorIds;
    
    protected OpTargetDoorSkillCondition(final IntSet doors) {
        this.doorIds = doors;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return GameUtils.isDoor(target) && this.doorIds.contains(target.getId());
    }
}
