// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerHasSummon extends Condition
{
    private final boolean _hasSummon;
    
    public ConditionPlayerHasSummon(final boolean hasSummon) {
        this._hasSummon = hasSummon;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        return player != null && this._hasSummon == player.hasSummon();
    }
}
