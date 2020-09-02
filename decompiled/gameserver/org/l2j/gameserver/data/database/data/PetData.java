// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("pets")
public class PetData
{
    @Column("item_obj_id")
    private int itemObjectId;
    private String name;
    private int level;
    private int curHp;
    private int curMp;
    private int exp;
    private int sp;
    private int fed;
    private int ownerId;
    private boolean restore;
    
    public int getItemObjectId() {
        return this.itemObjectId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public int getCurHp() {
        return this.curHp;
    }
    
    public int getCurMp() {
        return this.curMp;
    }
    
    public int getExp() {
        return this.exp;
    }
    
    public int getSp() {
        return this.sp;
    }
    
    public int getFed() {
        return this.fed;
    }
    
    public int getOwnerId() {
        return this.ownerId;
    }
    
    public boolean isRestore() {
        return this.restore;
    }
}
