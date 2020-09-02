// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.item.instance.Item;

public final class TempItem
{
    private final int _itemId;
    private final long _referencePrice;
    private final String _itemName;
    private int _quantity;
    
    public TempItem(final Item item, final int quantity) {
        this._itemId = item.getId();
        this._quantity = quantity;
        this._itemName = item.getTemplate().getName();
        this._referencePrice = item.getReferencePrice();
    }
    
    public int getQuantity() {
        return this._quantity;
    }
    
    public void setQuantity(final int quantity) {
        this._quantity = quantity;
    }
    
    public long getReferencePrice() {
        return this._referencePrice;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public String getItemName() {
        return this._itemName;
    }
}
