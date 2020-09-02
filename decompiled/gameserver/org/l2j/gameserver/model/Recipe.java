// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class Recipe
{
    private final int _itemId;
    private final int _quantity;
    
    public Recipe(final int itemId, final int quantity) {
        this._itemId = itemId;
        this._quantity = quantity;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public int getQuantity() {
        return this._quantity;
    }
}
