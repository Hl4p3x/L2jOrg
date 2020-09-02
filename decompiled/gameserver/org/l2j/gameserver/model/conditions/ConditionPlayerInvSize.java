// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.instance.Item;
import java.util.function.Predicate;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerInvSize extends Condition
{
    private final int _size;
    
    public ConditionPlayerInvSize(final int size) {
        this._size = size;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effector.getActingPlayer() == null || effector.getActingPlayer().getInventory().getSize(i -> !i.isQuestItem(), (Predicate<Item>[])new Predicate[0]) <= effector.getActingPlayer().getInventoryLimit() - this._size;
    }
}
