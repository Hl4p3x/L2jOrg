// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.options;

public final class VariationFee
{
    private final int _itemId;
    private final long _itemCount;
    private final long _cancelFee;
    
    public VariationFee(final int itemId, final long itemCount, final long cancelFee) {
        this._itemId = itemId;
        this._itemCount = itemCount;
        this._cancelFee = cancelFee;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public long getItemCount() {
        return this._itemCount;
    }
    
    public long getCancelFee() {
        return this._cancelFee;
    }
}
