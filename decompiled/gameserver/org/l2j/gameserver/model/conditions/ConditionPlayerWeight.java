// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerWeight extends Condition
{
    private final int _weight;
    
    public ConditionPlayerWeight(final int weight) {
        this._weight = weight;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        if (player != null && player.getMaxLoad() > 0) {
            final int weightproc = (player.getCurrentLoad() - player.getBonusWeightPenalty()) * 100 / player.getMaxLoad();
            return weightproc < this._weight || player.getDietMode();
        }
        return true;
    }
}
