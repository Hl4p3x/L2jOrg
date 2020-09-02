// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerFreight extends Warehouse
{
    private final Player _owner;
    private final int _ownerId;
    
    public PlayerFreight(final int object_id) {
        this._owner = null;
        this._ownerId = object_id;
        this.restore();
    }
    
    public PlayerFreight(final Player owner) {
        this._owner = owner;
        this._ownerId = owner.getObjectId();
    }
    
    @Override
    public int getOwnerId() {
        return this._ownerId;
    }
    
    @Override
    public Player getOwner() {
        return this._owner;
    }
    
    public ItemLocation getBaseLocation() {
        return ItemLocation.FREIGHT;
    }
    
    @Override
    public String getName() {
        return "Freight";
    }
    
    @Override
    public boolean validateCapacity(final long slots) {
        final int curSlots = (this._owner == null) ? Config.ALT_FREIGHT_SLOTS : Config.ALT_FREIGHT_SLOTS;
        return this.getSize() + slots <= curSlots;
    }
    
    public void refreshWeight() {
    }
    
    @Override
    public WarehouseType getType() {
        return WarehouseType.PRIVATE;
    }
}
