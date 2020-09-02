// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("clan_wars")
public class ClanWarData
{
    @Column("clan1")
    private int attacker;
    @Column("clan2")
    private int attacked;
    @Column("clan1Kill")
    private int attackerKills;
    @Column("clan2Kill")
    private int attackedKills;
    private int winnerClan;
    private long startTime;
    private long endTime;
    private ClanWarState state;
    
    public static ClanWarData of(final Clan attacker, final Clan attacked) {
        final ClanWarData data = new ClanWarData();
        data.attacker = attacker.getId();
        data.attacked = attacked.getId();
        data.startTime = System.currentTimeMillis();
        data.state = ClanWarState.BLOOD_DECLARATION;
        return data;
    }
    
    public int getAttacker() {
        return this.attacker;
    }
    
    public void setAttacker(final int attacker) {
        this.attacker = attacker;
    }
    
    public int getAttacked() {
        return this.attacked;
    }
    
    public void setAttacked(final int attacked) {
        this.attacked = attacked;
    }
    
    public int getAttackerKills() {
        return this.attackerKills;
    }
    
    public void setAttackerKills(final int attackerKills) {
        this.attackerKills = attackerKills;
    }
    
    public int getAttackedKills() {
        return this.attackedKills;
    }
    
    public void setAttackedKills(final int attackedKills) {
        this.attackedKills = attackedKills;
    }
    
    public int getWinnerClan() {
        return this.winnerClan;
    }
    
    public void setWinnerClan(final int winnerClan) {
        this.winnerClan = winnerClan;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }
    
    public ClanWarState getState() {
        return this.state;
    }
    
    public void setState(final ClanWarState state) {
        this.state = state;
    }
    
    public synchronized void incrementAttackerKill() {
        ++this.attackerKills;
    }
    
    public synchronized int incrementAttackedKill() {
        return ++this.attackedKills;
    }
}
