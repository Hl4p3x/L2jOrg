// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCp extends Condition
{
    private final int _cp;
    
    public ConditionPlayerCp(final int cp) {
        this._cp = cp;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector != null && effector.getCurrentCp() * 100.0 / effector.getMaxCp() >= this._cp;
    }
}
