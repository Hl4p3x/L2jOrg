// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("castle_manor_production")
public class SeedProduction
{
    @Column("castle_id")
    private int castleId;
    @Column("seed_id")
    private int seedId;
    private long amount;
    @Column("start_amount")
    private long startAmount;
    private long price;
    @Column("next_period")
    private boolean nextPeriod;
    
    public SeedProduction() {
    }
    
    public SeedProduction(final int id, final long amount, final long price, final long startAmount, final int castleId, final boolean nextPeriod) {
        this.seedId = id;
        this.amount = amount;
        this.price = price;
        this.startAmount = startAmount;
        this.castleId = castleId;
        this.nextPeriod = nextPeriod;
    }
    
    public final int getSeedId() {
        return this.seedId;
    }
    
    public final long getAmount() {
        return this.amount;
    }
    
    public final void setAmount(final long amount) {
        this.amount = amount;
    }
    
    public final long getPrice() {
        return this.price;
    }
    
    public final long getStartAmount() {
        return this.startAmount;
    }
    
    public boolean isNextPeriod() {
        return this.nextPeriod;
    }
    
    public synchronized boolean decreaseAmount(final long val) {
        if (this.amount - val < 0L) {
            return false;
        }
        this.amount -= val;
        return true;
    }
}
