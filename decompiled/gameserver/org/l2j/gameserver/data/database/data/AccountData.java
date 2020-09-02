// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.time.LocalDateTime;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("account_data")
public class AccountData
{
    @Column("account")
    private String accountName;
    private int coin;
    @Column("vip_point")
    private long vipPoints;
    @Column("vip_tier_expiration")
    private long vipTierExpiration;
    @Column("sec_auth_password")
    private String secAuthPassword;
    @Column("sec_auth_attempts")
    private int secAuthAttempts;
    @Column("next_attendance")
    private LocalDateTime nextAttendance;
    @Column("last_attendance_reward")
    private byte lastAttendanceReward;
    
    public static AccountData of(final String accountName) {
        final AccountData account = new AccountData();
        account.accountName = accountName;
        return account;
    }
    
    public String getAccountName() {
        return this.accountName;
    }
    
    public long getVipPoints() {
        return this.vipPoints;
    }
    
    public long getVipTierExpiration() {
        return this.vipTierExpiration;
    }
    
    public void updateVipPoints(final long points) {
        this.vipPoints += points;
    }
    
    public int getCoin() {
        return this.coin;
    }
    
    public void updateCoins(final int coins) {
        this.coin += coins;
    }
    
    public void setCoins(final int coins) {
        this.coin = coins;
    }
    
    public void setVipTierExpiration(final long expiration) {
        this.vipTierExpiration = expiration;
    }
    
    public String getSecAuthPassword() {
        return this.secAuthPassword;
    }
    
    public void setSecAuthPassword(final String secAuthPassword) {
        this.secAuthPassword = secAuthPassword;
    }
    
    public int getSecAuthAttempts() {
        return this.secAuthAttempts;
    }
    
    public void setSecAuthAttempts(final int secAuthAttempts) {
        this.secAuthAttempts = secAuthAttempts;
    }
    
    public int increaseSecAuthAttempts() {
        return ++this.secAuthAttempts;
    }
    
    public LocalDateTime nextAttendance() {
        return this.nextAttendance;
    }
    
    public void setNextAttendance(final LocalDateTime nextAttendance) {
        this.nextAttendance = nextAttendance;
    }
    
    public byte lastAttendanceReward() {
        return this.lastAttendanceReward;
    }
    
    public void setLastAttendanceReward(final byte reward) {
        this.lastAttendanceReward = reward;
    }
    
    @Override
    public String toString() {
        return this.accountName;
    }
}
