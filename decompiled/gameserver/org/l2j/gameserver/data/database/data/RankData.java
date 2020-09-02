// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

public class RankData
{
    @Column("id")
    private int playerId;
    @Column("name")
    private String playerName;
    private long exp;
    private byte level;
    @Column("class")
    private short classId;
    private byte race;
    @Column("clan_name")
    private String clanName;
    private int rank;
    @Column("rank_race")
    private int rankRace;
    @Column("rank_snapshot")
    private int rankSnapshot;
    @Column("rank_race_snapshot")
    private int rankRaceSnapshot;
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public int getRankRace() {
        return this.rankRace;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public long getExp() {
        return this.exp;
    }
    
    public byte getLevel() {
        return this.level;
    }
    
    public short getClassId() {
        return this.classId;
    }
    
    public byte getRace() {
        return this.race;
    }
    
    public String getClanName() {
        return this.clanName;
    }
    
    public int getRankSnapshot() {
        return this.rankSnapshot;
    }
    
    public int getRankRaceSnapshot() {
        return this.rankRaceSnapshot;
    }
}
