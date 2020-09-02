// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetLevelRange extends Condition
{
    public final int[] _levels;
    
    public ConditionTargetLevelRange(final int[] levels) {
        this._levels = levels;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (effected == null) {
            return false;
        }
        final int level = effected.getLevel();
        return level >= this._levels[0] && level <= this._levels[1];
    }
}
