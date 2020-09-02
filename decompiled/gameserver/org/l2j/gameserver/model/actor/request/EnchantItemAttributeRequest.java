// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;

public final class EnchantItemAttributeRequest extends AbstractRequest
{
    private volatile int _enchantingItemObjectId;
    private volatile int _enchantingStoneObjectId;
    
    public EnchantItemAttributeRequest(final Player activeChar, final int enchantingStoneObjectId) {
        super(activeChar);
        this._enchantingStoneObjectId = enchantingStoneObjectId;
    }
    
    public Item getEnchantingItem() {
        return this.getPlayer().getInventory().getItemByObjectId(this._enchantingItemObjectId);
    }
    
    public void setEnchantingItem(final int objectId) {
        this._enchantingItemObjectId = objectId;
    }
    
    public Item getEnchantingStone() {
        return this.getPlayer().getInventory().getItemByObjectId(this._enchantingStoneObjectId);
    }
    
    public void setEnchantingStone(final int objectId) {
        this._enchantingStoneObjectId = objectId;
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
        return objectId > 0 && (objectId == this._enchantingItemObjectId || objectId == this._enchantingStoneObjectId);
    }
}
