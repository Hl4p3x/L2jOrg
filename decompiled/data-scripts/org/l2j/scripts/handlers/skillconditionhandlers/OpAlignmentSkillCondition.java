// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.SkillConditionAlignment;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpAlignmentSkillCondition implements SkillCondition
{
    private final SkillConditionAffectType affectType;
    private final SkillConditionAlignment alignment;
    
    public OpAlignmentSkillCondition(final StatsSet params) {
        this.affectType = (SkillConditionAffectType)params.getEnum("affectType", (Class)SkillConditionAffectType.class);
        this.alignment = (SkillConditionAlignment)params.getEnum("alignment", (Class)SkillConditionAlignment.class);
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        boolean test = false;
        switch (this.affectType) {
            case CASTER: {
                test = this.alignment.test(caster.getActingPlayer());
                break;
            }
            case TARGET: {
                test = (GameUtils.isPlayer(target) && this.alignment.test(target.getActingPlayer()));
                break;
            }
            default: {
                test = false;
                break;
            }
        }
        return test;
    }
}
