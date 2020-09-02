// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("buylists")
public class BuyListInfo
{
    @Column("buylist_id")
    private int id;
    @Column("item_id")
    private int itemId;
    private long count;
    @Column("next_restock_time")
    private long nextRestock;
    
    public static BuyListInfo of(final int itemId, final int buyListId) {
        final BuyListInfo info = new BuyListInfo();
        info.id = buyListId;
        info.itemId = itemId;
        return info;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getItemId() {
        return this.itemId;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public void setCount(final long count) {
        this.count = count;
    }
    
    public long getNextRestock() {
        return this.nextRestock;
    }
    
    public void setNextRestock(final long nextRestock) {
        this.nextRestock = nextRestock;
    }
}
