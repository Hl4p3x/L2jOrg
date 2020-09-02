// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("clan_data")
public class ClanData
{
    @Column("clan_id")
    private int id;
    @Column("clan_name")
    private String name;
    @Column("clan_level")
    private int level;
    @Column("reputation_score")
    private int reputation;
    @Column("hasCastle")
    private int castle;
    @Column("blood_alliance_count")
    private int bloodAllianceCount;
    @Column("ally_id")
    private int allyId;
    @Column("ally_name")
    private String allyName;
    @Column("leader_id")
    private int leaderId;
    @Column("crest_id")
    private int crest;
    @Column("crest_large_id")
    private int crestLarge;
    @Column("ally_crest_id")
    private int allyCrest;
    @Column("ally_penalty_expiry_time")
    private long allyPenaltyExpiryTime;
    @Column("ally_penalty_type")
    private int allyPenaltyType;
    @Column("char_penalty_expiry_time")
    private long charPenaltyExpiryTime;
    @Column("dissolving_expiry_time")
    private long dissolvingExpiryTime;
    @Column("new_leader_id")
    private int newLeaderId;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int clanId) {
        this.id = clanId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String clanName) {
        this.name = clanName;
    }
    
    public int getAllyId() {
        return this.allyId;
    }
    
    public void setAllyId(final int allyId) {
        this.allyId = allyId;
    }
    
    public String getAllyName() {
        return this.allyName;
    }
    
    public void setAllyName(final String allyName) {
        this.allyName = allyName;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public void setLevel(final int level) {
        this.level = level;
    }
    
    public int getReputation() {
        return this.reputation;
    }
    
    public void setReputation(final int reputation) {
        this.reputation = reputation;
    }
    
    public int getCastle() {
        return this.castle;
    }
    
    public void setCastle(final int castleId) {
        this.castle = castleId;
    }
    
    public int getBloodAllianceCount() {
        return this.bloodAllianceCount;
    }
    
    public void setBloodAllianceCount(final int count) {
        this.bloodAllianceCount = count;
    }
    
    public int getLeaderId() {
        return this.leaderId;
    }
    
    public void setLeader(final int leaderId) {
        this.leaderId = leaderId;
    }
    
    public int getCrest() {
        return this.crest;
    }
    
    public void setCrest(final int crestId) {
        this.crest = crestId;
    }
    
    public int getCrestLarge() {
        return this.crestLarge;
    }
    
    public void setCrestLarge(final int crestLargeId) {
        this.crestLarge = crestLargeId;
    }
    
    public int getAllyCrest() {
        return this.allyCrest;
    }
    
    public void setAllyCrest(final int allyCrestId) {
        this.allyCrest = allyCrestId;
    }
    
    public long getAllyPenaltyExpiryTime() {
        return this.allyPenaltyExpiryTime;
    }
    
    public void setAllyPenaltyExpiryTime(final long expiryTime) {
        this.allyPenaltyExpiryTime = expiryTime;
    }
    
    public int getAllyPenaltyType() {
        return this.allyPenaltyType;
    }
    
    public void setAllyPenaltyType(final int penaltyType) {
        this.allyPenaltyType = penaltyType;
    }
    
    public long getCharPenaltyExpiryTime() {
        return this.charPenaltyExpiryTime;
    }
    
    public void setCharPenaltyExpiryTime(final long time) {
        this.charPenaltyExpiryTime = time;
    }
    
    public long getDissolvingExpiryTime() {
        return this.dissolvingExpiryTime;
    }
    
    public void setDissolvingExpiryTime(final long time) {
        this.dissolvingExpiryTime = time;
    }
    
    public int getNewLeaderId() {
        return this.newLeaderId;
    }
    
    public void setNewLeader(final int leaderId) {
        this.leaderId = leaderId;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.name, this.id);
    }
}
