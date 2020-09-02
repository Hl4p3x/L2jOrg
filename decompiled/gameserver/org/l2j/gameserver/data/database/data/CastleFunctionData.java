// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;

@Table("castle_functions")
public class CastleFunctionData
{
    private int type;
    private int level;
    private int lease;
    private long rate;
    private long endTime;
    
    public CastleFunctionData() {
    }
    
    public CastleFunctionData(final int type, final int level, final int lease, final long rate, final int time) {
        this.type = type;
        this.level = level;
        this.lease = lease;
        this.rate = rate;
        this.endTime = time;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public void setLevel(final int lvl) {
        this.level = lvl;
    }
    
    public int getLease() {
        return this.lease;
    }
    
    public void setLease(final int lease) {
        this.lease = lease;
    }
    
    public long getRate() {
        return this.rate;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final long time) {
        this.endTime = time;
    }
}
