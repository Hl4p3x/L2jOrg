// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class ItemRequest
{
    int _objectId;
    int _itemId;
    long _count;
    long _price;
    
    public ItemRequest(final int objectId, final long count, final long price) {
        this._objectId = objectId;
        this._count = count;
        this._price = price;
    }
    
    public ItemRequest(final int objectId, final int itemId, final long count, final long price) {
        this._objectId = objectId;
        this._itemId = itemId;
        this._count = count;
        this._price = price;
    }
    
    public int getObjectId() {
        return this._objectId;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public long getCount() {
        return this._count;
    }
    
    public void setCount(final long count) {
        this._count = count;
    }
    
    public long getPrice() {
        return this._price;
    }
    
    @Override
    public int hashCode() {
        return this._objectId;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof ItemRequest && this._objectId != ((ItemRequest)obj)._objectId);
    }
}
