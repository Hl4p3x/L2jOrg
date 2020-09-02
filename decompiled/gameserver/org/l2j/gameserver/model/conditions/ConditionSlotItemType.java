// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public final class ConditionSlotItemType extends ConditionInventory
{
    private final int _mask;
    
    public ConditionSlotItemType(final int slot, final int mask) {
        super(slot);
        this._mask = mask;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (!GameUtils.isPlayer(effector)) {
            return false;
        }
        final Item itemSlot = effector.getInventory().getPaperdollItem(InventorySlot.fromId(this._slot));
        return itemSlot != null && (itemSlot.getTemplate().getItemMask() & this._mask) != 0x0;
    }
}
