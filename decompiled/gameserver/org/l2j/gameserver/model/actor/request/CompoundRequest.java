// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;

public class CompoundRequest extends AbstractRequest
{
    private int _itemOne;
    private int _itemTwo;
    
    public CompoundRequest(final Player activeChar) {
        super(activeChar);
    }
    
    public Item getItemOne() {
        return this.getPlayer().getInventory().getItemByObjectId(this._itemOne);
    }
    
    public void setItemOne(final int itemOne) {
        this._itemOne = itemOne;
    }
    
    public Item getItemTwo() {
        return this.getPlayer().getInventory().getItemByObjectId(this._itemTwo);
    }
    
    public void setItemTwo(final int itemTwo) {
        this._itemTwo = itemTwo;
    }
    
    @Override
    public boolean isItemRequest() {
        return true;
    }
    
    @Override
    public boolean canWorkWith(final AbstractRequest request) {
        return !request.isItemRequest();
    }
    
    @Override
    public boolean isUsing(final int objectId) {
        return objectId > 0 && (objectId == this._itemOne || objectId == this._itemTwo);
    }
}
