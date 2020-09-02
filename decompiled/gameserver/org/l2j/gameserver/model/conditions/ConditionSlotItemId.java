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

public final class ConditionSlotItemId extends ConditionInventory
{
    private final int _itemId;
    private final int _enchantLevel;
    
    public ConditionSlotItemId(final int slot, final int itemId, final int enchantLevel) {
        super(slot);
        this._itemId = itemId;
        this._enchantLevel = enchantLevel;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (!GameUtils.isPlayer(effector)) {
            return false;
        }
        final Item itemSlot = effector.getInventory().getPaperdollItem(InventorySlot.fromId(this._slot));
        if (itemSlot == null) {
            return this._itemId == 0;
        }
        return itemSlot.getId() == this._itemId && itemSlot.getEnchantLevel() >= this._enchantLevel;
    }
}
