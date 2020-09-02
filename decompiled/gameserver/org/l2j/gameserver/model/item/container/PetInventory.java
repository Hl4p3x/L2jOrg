// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import java.util.Iterator;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Pet;

public class PetInventory extends Inventory
{
    private final Pet _owner;
    
    public PetInventory(final Pet owner) {
        this._owner = owner;
    }
    
    @Override
    public Pet getOwner() {
        return this._owner;
    }
    
    @Override
    public int getOwnerId() {
        int id;
        try {
            id = this._owner.getOwner().getObjectId();
        }
        catch (NullPointerException e) {
            return 0;
        }
        return id;
    }
    
    @Override
    protected void refreshWeight() {
        super.refreshWeight();
        this._owner.updateAndBroadcastStatus(1);
    }
    
    public boolean validateCapacity(final Item item) {
        int slots = 0;
        if ((!item.isStackable() || this.getItemByItemId(item.getId()) == null) && !item.getTemplate().hasExImmediateEffect()) {
            ++slots;
        }
        return this.validateCapacity(slots);
    }
    
    @Override
    public boolean validateCapacity(final long slots) {
        return this.items.size() + slots <= this._owner.getInventoryLimit();
    }
    
    public boolean validateWeight(final Item item, final long count) {
        int weight = 0;
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(item.getId());
        if (template == null) {
            return false;
        }
        weight += (int)(count * template.getWeight());
        return this.validateWeight(weight);
    }
    
    @Override
    public boolean validateWeight(final long weight) {
        return this._totalWeight + weight <= this._owner.getMaxLoad();
    }
    
    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.PET;
    }
    
    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PET_EQUIP;
    }
    
    @Override
    public void restore() {
        super.restore();
        for (final Item item : this.items.values()) {
            if (item.isEquipped() && !item.getTemplate().checkCondition(this._owner, this._owner, false)) {
                this.unEquipItemInSlot(InventorySlot.fromId(item.getLocationSlot()));
            }
        }
    }
    
    public void transferItemsToOwner() {
        for (final Item item : this.items.values()) {
            this.getOwner().transferItem("return", item.getObjectId(), item.getCount(), this.getOwner().getOwner().getInventory(), this.getOwner().getOwner(), this.getOwner());
        }
    }
}
