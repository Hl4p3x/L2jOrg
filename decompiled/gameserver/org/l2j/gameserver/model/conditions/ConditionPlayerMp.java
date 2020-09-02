// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerMp extends Condition
{
    private final int _mp;
    
    public ConditionPlayerMp(final int mp) {
        this._mp = mp;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getCurrentMp() * 100.0 / effector.getMaxMp() <= this._mp;
    }
}
