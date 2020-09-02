// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Objects;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.type.ArmorType;

public final class ConditionUsingItemType extends Condition
{
    public final boolean _armor;
    public final int _mask;
    
    public ConditionUsingItemType(final int mask) {
        this._mask = mask;
        this._armor = ((this._mask & (ArmorType.MAGIC.mask() | ArmorType.LIGHT.mask() | ArmorType.HEAVY.mask())) != 0x0);
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (Objects.isNull(effector)) {
            return false;
        }
        if (!GameUtils.isPlayer(effector)) {
            return !this._armor && (this._mask & effector.getAttackType().mask()) != 0x0;
        }
        final Inventory inv = effector.getInventory();
        if (!this._armor) {
            return (this._mask & inv.getWearedMask()) != 0x0;
        }
        final Item chest = inv.getPaperdollItem(InventorySlot.CHEST);
        if (chest == null) {
            return (ArmorType.NONE.mask() & this._mask) == ArmorType.NONE.mask();
        }
        final int chestMask = chest.getTemplate().getItemMask();
        if ((this._mask & chestMask) == 0x0) {
            return false;
        }
        final BodyPart chestBodyPart = chest.getBodyPart();
        if (chestBodyPart == BodyPart.FULL_ARMOR) {
            return true;
        }
        final Item legs = inv.getPaperdollItem(InventorySlot.LEGS);
        if (legs == null) {
            return (ArmorType.NONE.mask() & this._mask) == ArmorType.NONE.mask();
        }
        final int legMask = legs.getTemplate().getItemMask();
        return (this._mask & legMask) != 0x0;
    }
}
