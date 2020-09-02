// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

public class RankHistoryData
{
    @Column("id")
    private int playerId;
    private long exp;
    private int rank;
    private int date;
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public long getExp() {
        return this.exp;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public int getDate() {
        return this.date;
    }
}
