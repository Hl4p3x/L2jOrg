// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerWarehouse extends Warehouse
{
    private final Player _owner;
    
    public PlayerWarehouse(final Player owner) {
        this._owner = owner;
    }
    
    @Override
    public String getName() {
        return "Warehouse";
    }
    
    @Override
    public Player getOwner() {
        return this._owner;
    }
    
    public ItemLocation getBaseLocation() {
        return ItemLocation.WAREHOUSE;
    }
    
    @Override
    public boolean validateCapacity(final long slots) {
        return this.items.size() + slots <= this._owner.getWareHouseLimit();
    }
    
    @Override
    public WarehouseType getType() {
        return WarehouseType.PRIVATE;
    }
}
