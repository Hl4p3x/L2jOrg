// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerSouls extends Condition
{
    private final int _souls;
    
    public ConditionPlayerSouls(final int souls) {
        this._souls = souls;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() != null && effector.getActingPlayer().getChargedSouls() >= this._souls;
    }
}
