// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

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
    private String loc;
    @Column("loc_data")
    private int locData;
    @Column("time_of_use")
    private int timeOfUse;
    @Column("custom_type1")
    private int customType1;
    @Column("custom_type2")
    private int customType2;
    private float time;
    
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
    
    public String getLoc() {
        return this.loc;
    }
    
    public int getLocData() {
        return this.locData;
    }
    
    public int getTimeOfUse() {
        return this.timeOfUse;
    }
    
    public int getCustomType1() {
        return this.customType1;
    }
    
    public int getCustomType2() {
        return this.customType2;
    }
    
    public float getTime() {
        return this.time;
    }
}
