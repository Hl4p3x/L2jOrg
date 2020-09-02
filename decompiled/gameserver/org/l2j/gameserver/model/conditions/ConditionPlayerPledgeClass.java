// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public final class ConditionPlayerPledgeClass extends Condition
{
    public final int _pledgeClass;
    
    public ConditionPlayerPledgeClass(final int pledgeClass) {
        this._pledgeClass = pledgeClass;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        return !Objects.isNull(player) && !Objects.isNull(player.getClan()) && (player.isClanLeader() || (this._pledgeClass != -1 && player.getPledgeClass() >= this._pledgeClass));
    }
}
