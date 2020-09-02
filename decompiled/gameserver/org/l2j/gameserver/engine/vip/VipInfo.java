// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.vip;

class VipInfo
{
    private final byte tier;
    private final long pointsRequired;
    private final long pointsDepreciated;
    private float silverCoinChance;
    private float rustyCoinChance;
    private int skill;
    
    VipInfo(final byte tier, final long pointsRequired, final long pointsDepreciated) {
        this.tier = tier;
        this.pointsRequired = pointsRequired;
        this.pointsDepreciated = pointsDepreciated;
    }
    
    byte getTier() {
        return this.tier;
    }
    
    void setSilverCoinChance(final float silverCoinChance) {
        this.silverCoinChance = silverCoinChance;
    }
    
    float getSilverCoinChance() {
        return this.silverCoinChance;
    }
    
    void setRustyCoinChance(final float rustyCoinChance) {
        this.rustyCoinChance = rustyCoinChance;
    }
    
    float getRustyCoinChance() {
        return this.rustyCoinChance;
    }
    
    long getPointsRequired() {
        return this.pointsRequired;
    }
    
    long getPointsDepreciated() {
        return this.pointsDepreciated;
    }
    
    int getSkill() {
        return this.skill;
    }
    
    void setSkill(final int skill) {
        this.skill = skill;
    }
}
