// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class TargetRaceSkillCondition implements SkillCondition
{
    public final Race race;
    
    protected TargetRaceSkillCondition(final Race race) {
        this.race = race;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isCreature(target)) {
            return false;
        }
        final Creature targetCreature = (Creature)target;
        return targetCreature.getRace() == this.race;
    }
}
