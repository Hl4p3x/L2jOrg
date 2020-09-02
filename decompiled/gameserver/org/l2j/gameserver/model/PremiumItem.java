// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class PremiumItem
{
    private final int _itemId;
    private final String _sender;
    private long _count;
    
    public PremiumItem(final int itemid, final long count, final String sender) {
        this._itemId = itemid;
        this._count = count;
        this._sender = sender;
    }
    
    public void updateCount(final long newcount) {
        this._count = newcount;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public long getCount() {
        return this._count;
    }
    
    public String getSender() {
        return this._sender;
    }
}
