// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.auction;

import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.model.StatsSet;
import java.util.Calendar;

public final class AuctionDateGenerator
{
    public static final String FIELD_INTERVAL = "interval";
    public static final String FIELD_DAY_OF_WEEK = "day_of_week";
    public static final String FIELD_HOUR_OF_DAY = "hour_of_day";
    public static final String FIELD_MINUTE_OF_HOUR = "minute_of_hour";
    private static final long MILLIS_IN_WEEK;
    private final Calendar _calendar;
    private final int _interval;
    private int _day_of_week;
    private int _hour_of_day;
    private int _minute_of_hour;
    
    public AuctionDateGenerator(final StatsSet config) throws IllegalArgumentException {
        this._calendar = Calendar.getInstance();
        this._interval = config.getInt("interval", -1);
        final int fixedDayWeek = config.getInt("day_of_week", -1) + 1;
        this._day_of_week = ((fixedDayWeek > 7) ? 1 : fixedDayWeek);
        this._hour_of_day = config.getInt("hour_of_day", -1);
        this._minute_of_hour = config.getInt("minute_of_hour", -1);
        this.checkDayOfWeek(-1);
        this.checkHourOfDay(-1);
        this.checkMinuteOfHour(0);
    }
    
    public final synchronized long nextDate(final long date) {
        this._calendar.setTimeInMillis(date);
        this._calendar.set(14, 0);
        this._calendar.set(13, 0);
        this._calendar.set(12, this._minute_of_hour);
        this._calendar.set(11, this._hour_of_day);
        if (this._day_of_week > 0) {
            this._calendar.set(7, this._day_of_week);
            return this.calcDestTime(this._calendar.getTimeInMillis(), date, AuctionDateGenerator.MILLIS_IN_WEEK);
        }
        return this.calcDestTime(this._calendar.getTimeInMillis(), date, TimeUnit.MILLISECONDS.convert(this._interval, TimeUnit.DAYS));
    }
    
    private long calcDestTime(long time, final long date, final long add) {
        if (time < date) {
            time += (date - time) / add * add;
            if (time < date) {
                time += add;
            }
        }
        return time;
    }
    
    private void checkDayOfWeek(final int defaultValue) {
        if (this._day_of_week < 1 || this._day_of_week > 7) {
            if (defaultValue == -1 && this._interval < 1) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/io/Serializable;)Ljava/lang/String;, (this._day_of_week == -1) ? "not found" : Integer.valueOf(this._day_of_week)));
            }
            this._day_of_week = defaultValue;
        }
        else if (this._interval > 1) {
            throw new IllegalArgumentException("Illegal params for 'interval' and 'day_of_week': you can use only one, not both");
        }
    }
    
    private void checkHourOfDay(final int defaultValue) {
        if (this._hour_of_day < 0 || this._hour_of_day > 23) {
            if (defaultValue == -1) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/io/Serializable;)Ljava/lang/String;, (this._hour_of_day == -1) ? "not found" : Integer.valueOf(this._hour_of_day)));
            }
            this._hour_of_day = defaultValue;
        }
    }
    
    private void checkMinuteOfHour(final int defaultValue) {
        if (this._minute_of_hour < 0 || this._minute_of_hour > 59) {
            if (defaultValue == -1) {
                throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(Ljava/io/Serializable;)Ljava/lang/String;, (this._minute_of_hour == -1) ? "not found" : Integer.valueOf(this._minute_of_hour)));
            }
            this._minute_of_hour = defaultValue;
        }
    }
    
    static {
        MILLIS_IN_WEEK = TimeUnit.MILLISECONDS.convert(7L, TimeUnit.DAYS);
    }
}
