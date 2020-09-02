// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("player_stats_points")
public class PlayerStatsData
{
    @Column("player_id")
    private int playerId;
    private short points;
    private short strength;
    private short dexterity;
    private short constitution;
    private short intelligence;
    private short witness;
    private short mentality;
    
    public static PlayerStatsData init(final int playerId) {
        final PlayerStatsData data = new PlayerStatsData();
        data.playerId = playerId;
        return data;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public short getPoints() {
        return this.points;
    }
    
    public void setPoints(final short points) {
        this.points = points;
    }
    
    public boolean update(final short str, final short dex, final short con, final short intt, final short wit, final short men) {
        if (this.strength + this.dexterity + this.constitution + this.intelligence + this.witness + this.mentality + str + dex + con + intt + wit + men <= this.points) {
            this.strength += str;
            this.dexterity += dex;
            this.constitution += con;
            this.intelligence += intt;
            this.witness += wit;
            this.mentality += men;
            return true;
        }
        return false;
    }
    
    public void reset() {
        final short n = 0;
        this.mentality = n;
        this.witness = n;
        this.intelligence = n;
        this.constitution = n;
        this.dexterity = n;
        this.strength = n;
    }
    
    public int getValue(final BaseStats stat) {
        short n = 0;
        switch (stat) {
            case CON: {
                n = this.constitution;
                break;
            }
            case DEX: {
                n = this.dexterity;
                break;
            }
            case MEN: {
                n = this.mentality;
                break;
            }
            case STR: {
                n = this.strength;
                break;
            }
            case WIT: {
                n = this.witness;
                break;
            }
            case INT: {
                n = this.intelligence;
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        return n;
    }
}
