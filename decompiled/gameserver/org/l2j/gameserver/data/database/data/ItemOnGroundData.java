// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("itemsonground")
public class ItemOnGroundData
{
    @Column("object_id")
    private int objectId;
    @Column("item_id")
    private int itemId;
    private long count;
    @Column("enchant_level")
    private int enchantLevel;
    private int x;
    private int y;
    private int z;
    @Column("drop_time")
    private long dropTime;
    private int equipable;
    
    public static ItemOnGroundData of(final Item item) {
        final ItemOnGroundData data = new ItemOnGroundData();
        data.objectId = item.getObjectId();
        data.itemId = item.getId();
        data.count = item.getCount();
        data.enchantLevel = item.getEnchantLevel();
        data.x = item.getX();
        data.y = item.getY();
        data.z = item.getZ();
        data.dropTime = (item.isProtected() ? -1L : item.getDropTime());
        data.equipable = (item.isEquipable() ? 1 : 0);
        return data;
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
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public long getDropTime() {
        return this.dropTime;
    }
}
