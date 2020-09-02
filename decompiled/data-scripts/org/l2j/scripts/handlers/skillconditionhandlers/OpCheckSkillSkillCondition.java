// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpCheckSkillSkillCondition implements SkillCondition
{
    private final int _skillId;
    private final SkillConditionAffectType _affectType;
    
    public OpCheckSkillSkillCondition(final StatsSet params) {
        this._skillId = params.getInt("skillId");
        this._affectType = (SkillConditionAffectType)params.getEnum("affectType", (Class)SkillConditionAffectType.class);
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        switch (this._affectType) {
            case CASTER: {
                return caster.getSkillLevel(this._skillId) > 0;
            }
            case TARGET: {
                if (target != null && !GameUtils.isPlayer(target)) {
                    return target.getActingPlayer().getSkillLevel(this._skillId) > 0;
                }
                break;
            }
        }
        return false;
    }
}
