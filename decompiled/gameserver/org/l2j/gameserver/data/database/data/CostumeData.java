// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("player_costumes")
public class CostumeData
{
    @Column("player_id")
    private int playerId;
    private int id;
    private long amount;
    private boolean locked;
    @NonUpdatable
    private boolean isNew;
    
    public void increaseAmount() {
        ++this.amount;
    }
    
    public static CostumeData of(final int costumeId, final Player player) {
        final CostumeData data = new CostumeData();
        data.playerId = player.getObjectId();
        data.id = costumeId;
        data.isNew = true;
        return data;
    }
    
    public int getId() {
        return this.id;
    }
    
    public long getAmount() {
        return this.amount;
    }
    
    public void setLocked(final boolean lock) {
        this.locked = lock;
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void reduceCount(final long amount) {
        this.amount -= amount;
    }
    
    public boolean checkIsNewAndChange() {
        final boolean ret = this.isNew;
        this.isNew = false;
        return ret;
    }
}
