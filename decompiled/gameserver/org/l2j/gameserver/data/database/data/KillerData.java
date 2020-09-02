// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

public class KillerData
{
    @Column("killer_id")
    private int killeId;
    private String name;
    private String clan;
    private int level;
    private int race;
    @Column("active_class")
    private int activeClass;
    @Column("kill_time")
    private int killTime;
    private boolean online;
    
    public int getKilleId() {
        return this.killeId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getClan() {
        return this.clan;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public int getRace() {
        return this.race;
    }
    
    public int getActiveClass() {
        return this.activeClass;
    }
    
    public int getKillTime() {
        return this.killTime;
    }
    
    public boolean isOnline() {
        return this.online;
    }
}
