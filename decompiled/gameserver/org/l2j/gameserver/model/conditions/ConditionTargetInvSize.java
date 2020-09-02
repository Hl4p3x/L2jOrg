// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.function.Predicate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionTargetInvSize extends Condition
{
    private final int _size;
    
    public ConditionTargetInvSize(final int size) {
        this._size = size;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (GameUtils.isPlayer(effected)) {
            final Player target = effected.getActingPlayer();
            return target.getInventory().getSize(i -> !i.isQuestItem(), (Predicate<Item>[])new Predicate[0]) <= target.getInventoryLimit() - this._size;
        }
        return false;
    }
}
