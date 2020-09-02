// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.ArrayList;

public class ConditionPlayerHasPet extends Condition
{
    private final ArrayList<Integer> _controlItemIds;
    
    public ConditionPlayerHasPet(final ArrayList<Integer> itemIds) {
        if (itemIds.size() == 1 && itemIds.get(0) == 0) {
            this._controlItemIds = null;
        }
        else {
            this._controlItemIds = itemIds;
        }
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Summon pet = effector.getActingPlayer().getPet();
        if (effector.getActingPlayer() == null || pet == null) {
            return false;
        }
        if (this._controlItemIds == null) {
            return true;
        }
        final Item controlItem = ((Pet)pet).getControlItem();
        return controlItem != null && this._controlItemIds.contains(controlItem.getId());
    }
}
