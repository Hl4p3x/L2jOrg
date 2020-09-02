// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats;

import java.time.temporal.TemporalAmount;
import java.time.temporal.Temporal;
import java.time.Instant;
import java.time.Duration;

public class BasicPropertyResist
{
    private static final Duration RESIST_DURATION;
    private volatile Instant _resistanceEndTime;
    private volatile int _resistanceLevel;
    
    public BasicPropertyResist() {
        this._resistanceEndTime = Instant.MIN;
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(this._resistanceEndTime);
    }
    
    public Duration getRemainTime() {
        return Duration.between(Instant.now(), this._resistanceEndTime);
    }
    
    public int getResistLevel() {
        return this.isExpired() ? 0 : this._resistanceLevel;
    }
    
    public synchronized void increaseResistLevel() {
        if (this.isExpired()) {
            this._resistanceLevel = 1;
            this._resistanceEndTime = Instant.now().plus((TemporalAmount)BasicPropertyResist.RESIST_DURATION);
        }
        else {
            ++this._resistanceLevel;
        }
    }
    
    static {
        RESIST_DURATION = Duration.ofSeconds(15L);
    }
}
