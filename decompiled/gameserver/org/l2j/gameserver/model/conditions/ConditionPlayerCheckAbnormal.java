// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.AbnormalType;

public class ConditionPlayerCheckAbnormal extends Condition
{
    private final AbnormalType _type;
    private final int _level;
    
    public ConditionPlayerCheckAbnormal(final AbnormalType type) {
        this._type = type;
        this._level = -1;
    }
    
    public ConditionPlayerCheckAbnormal(final AbnormalType type, final int level) {
        this._type = type;
        this._level = level;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (this._level == -1) {
            return effector.getEffectList().hasAbnormalType(this._type);
        }
        return effector.getEffectList().hasAbnormalType(this._type, info -> this._level >= info.getSkill().getAbnormalLvl());
    }
}
