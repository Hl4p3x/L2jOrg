// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanSummonServitor extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCanSummonServitor(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        if (player == null) {
            return false;
        }
        boolean canSummon = true;
        if (player.isFlyingMounted() || player.isMounted() || player.inObserverMode() || player.isTeleporting()) {
            canSummon = false;
        }
        else if (player.getServitors().size() >= 4) {
            canSummon = false;
        }
        return canSummon == this._val;
    }
}
