// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.ArrayList;

public class ConditionPlayerInstanceId extends Condition
{
    private final ArrayList<Integer> _instanceIds;
    
    public ConditionPlayerInstanceId(final ArrayList<Integer> instanceIds) {
        this._instanceIds = instanceIds;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        if (player == null) {
            return false;
        }
        final Instance instance = player.getInstanceWorld();
        return instance != null && this._instanceIds.contains(instance.getTemplateId());
    }
}
