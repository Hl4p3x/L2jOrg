// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.AbnormalType;

public class ConditionTargetAbnormalType extends Condition
{
    private final AbnormalType _abnormalType;
    
    public ConditionTargetAbnormalType(final AbnormalType abnormalType) {
        this._abnormalType = abnormalType;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effected.hasAbnormalType(this._abnormalType);
    }
}
