// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("items")
public class ItemData
{
    @Column("owner_id")
    private int ownerId;
    @Column("object_id")
    private int objectId;
    @Column("item_id")
    private int itemId;
    private long count;
    @Column("enchant_level")
    private int enchantLevel;
    private ItemLocation loc;
    @Column("loc_data")
    private int locData;
    @Column("time_of_use")
    private int timeOfUse;
    private int time;
    
    public int getOwnerId() {
        return this.ownerId;
    }
    
    public int getObjectId() {
        return this.objectId;
    }
    
    public int getItemId() {
        return this.itemId;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public int getEnchantLevel() {
        return this.enchantLevel;
    }
    
    public ItemLocation getLoc() {
        return this.loc;
    }
    
    public int getLocData() {
        return this.locData;
    }
    
    public int getTimeOfUse() {
        return this.timeOfUse;
    }
    
    public float getTime() {
        return (float)this.time;
    }
}
