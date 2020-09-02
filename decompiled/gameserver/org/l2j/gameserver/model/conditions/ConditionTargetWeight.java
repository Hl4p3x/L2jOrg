// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetWeight extends Condition
{
    private final int _weight;
    
    public ConditionTargetWeight(final int weight) {
        this._weight = weight;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (GameUtils.isPlayer(effected)) {
            final Player target = effected.getActingPlayer();
            if (!target.getDietMode() && target.getMaxLoad() > 0) {
                final int weightproc = (target.getCurrentLoad() - target.getBonusWeightPenalty()) * 100 / target.getMaxLoad();
                return weightproc < this._weight;
            }
        }
        return false;
    }
}
