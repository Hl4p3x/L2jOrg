// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.concurrent.TimeUnit;
import java.time.DayOfWeek;

public final class InstanceReenterTimeHolder
{
    private DayOfWeek _day;
    private int _hour;
    private int _minute;
    private long _time;
    
    public InstanceReenterTimeHolder(final long time) {
        this._day = null;
        this._hour = -1;
        this._minute = -1;
        this._time = -1L;
        this._time = TimeUnit.MINUTES.toMillis(time);
    }
    
    public InstanceReenterTimeHolder(final DayOfWeek day, final int hour, final int minute) {
        this._day = null;
        this._hour = -1;
        this._minute = -1;
        this._time = -1L;
        this._day = day;
        this._hour = hour;
        this._minute = minute;
    }
    
    public final long getTime() {
        return this._time;
    }
    
    public final DayOfWeek getDay() {
        return this._day;
    }
    
    public final int getHour() {
        return this._hour;
    }
    
    public final int getMinute() {
        return this._minute;
    }
}
