// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetActiveEffectId extends Condition
{
    private final int _effectId;
    private final int _effectLvl;
    
    public ConditionTargetActiveEffectId(final int effectId) {
        this._effectId = effectId;
        this._effectLvl = -1;
    }
    
    public ConditionTargetActiveEffectId(final int effectId, final int effectLevel) {
        this._effectId = effectId;
        this._effectLvl = effectLevel;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final BuffInfo info = effected.getEffectList().getBuffInfoBySkillId(this._effectId);
        return info != null && (this._effectLvl == -1 || this._effectLvl <= info.getSkill().getLevel());
    }
}
