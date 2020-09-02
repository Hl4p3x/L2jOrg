// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.time.DayOfWeek;

public class SiegeScheduleDate
{
    private final DayOfWeek day;
    private final int hour;
    private final int maxConcurrent;
    
    public SiegeScheduleDate(final DayOfWeek day, final int hour, final int maxConcurrent) {
        this.day = day;
        this.hour = hour;
        this.maxConcurrent = maxConcurrent;
    }
    
    public SiegeScheduleDate() {
        this.day = DayOfWeek.SUNDAY;
        this.hour = 20;
        this.maxConcurrent = 5;
    }
    
    public DayOfWeek getDay() {
        return this.day;
    }
    
    public int getHour() {
        return this.hour;
    }
    
    public int getMaxConcurrent() {
        return this.maxConcurrent;
    }
}
