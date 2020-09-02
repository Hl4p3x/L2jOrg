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

public class OpPkcountSkillCondition implements SkillCondition
{
    private final SkillConditionAffectType affectType;
    
    public OpPkcountSkillCondition(final StatsSet params) {
        this.affectType = (SkillConditionAffectType)params.getEnum("affectType", (Class)SkillConditionAffectType.class);
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        boolean b = false;
        switch (this.affectType) {
            case CASTER: {
                b = (GameUtils.isPlayer((WorldObject)caster) && caster.getActingPlayer().getPkKills() > 0);
                break;
            }
            case TARGET: {
                b = (GameUtils.isPlayer(target) && target.getActingPlayer().getPkKills() > 0);
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
}
