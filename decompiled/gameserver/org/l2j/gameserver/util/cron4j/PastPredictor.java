// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.GregorianCalendar;
import java.util.Date;
import java.util.TimeZone;

public class PastPredictor
{
    private final SchedulingPattern _schedulingPattern;
    private long _time;
    private TimeZone _timeZone;
    
    public PastPredictor(final String schedulingPattern, final long start) throws InvalidPatternException {
        this._timeZone = TimeZone.getDefault();
        this._schedulingPattern = new SchedulingPattern(schedulingPattern);
        this._time = start / 60000L * 1000L * 60L;
    }
    
    public PastPredictor(final String schedulingPattern, final Date start) throws InvalidPatternException {
        this(schedulingPattern, start.getTime());
    }
    
    public PastPredictor(final String schedulingPattern) throws InvalidPatternException {
        this(schedulingPattern, System.currentTimeMillis());
    }
    
    public PastPredictor(final SchedulingPattern schedulingPattern, final long start) {
        this._timeZone = TimeZone.getDefault();
        this._schedulingPattern = schedulingPattern;
        this._time = start / 60000L * 1000L * 60L;
    }
    
    public PastPredictor(final SchedulingPattern schedulingPattern, final Date start) {
        this(schedulingPattern, start.getTime());
    }
    
    public PastPredictor(final SchedulingPattern schedulingPattern) {
        this(schedulingPattern, System.currentTimeMillis());
    }
    
    public void setTimeZone(final TimeZone timeZone) {
        this._timeZone = timeZone;
    }
    
    public synchronized long prevMatchingTime() {
        this._time -= 60000L;
        if (this._schedulingPattern.match(this._time)) {
            return this._time;
        }
        final int size = this._schedulingPattern.matcherSize;
        final long[] times = new long[size];
        for (int k = 0; k < size; ++k) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(this._time);
            c.setTimeZone(this._timeZone);
            int minute = c.get(12);
            int hour = c.get(11);
            int dayOfMonth = c.get(5);
            int month = c.get(2);
            int year = c.get(1);
            final ValueMatcher minuteMatcher = this._schedulingPattern.minuteMatchers.get(k);
            final ValueMatcher hourMatcher = this._schedulingPattern.hourMatchers.get(k);
            final ValueMatcher dayOfMonthMatcher = this._schedulingPattern.dayOfMonthMatchers.get(k);
            final ValueMatcher dayOfWeekMatcher = this._schedulingPattern.dayOfWeekMatchers.get(k);
            final ValueMatcher monthMatcher = this._schedulingPattern.monthMatchers.get(k);
            while (true) {
                if (minuteMatcher.match(minute)) {
                    if (hour < 0) {
                        hour = 23;
                        --dayOfMonth;
                    }
                    if (hourMatcher.match(hour)) {
                        if (dayOfMonth < 1) {
                            dayOfMonth = 31;
                            --month;
                        }
                        if (month < 0) {
                            month = 11;
                            --year;
                        }
                        if (dayOfMonthMatcher instanceof DayOfMonthValueMatcher) {
                            final DayOfMonthValueMatcher aux = (DayOfMonthValueMatcher)dayOfMonthMatcher;
                            if (!aux.match(dayOfMonth, month + 1, c.isLeapYear(year))) {
                                --dayOfMonth;
                                hour = 23;
                                minute = 59;
                                continue;
                            }
                        }
                        else if (!dayOfMonthMatcher.match(dayOfMonth)) {
                            --dayOfMonth;
                            hour = 23;
                            minute = 59;
                            continue;
                        }
                        if (monthMatcher.match(month + 1)) {
                            c = new GregorianCalendar();
                            c.setTimeZone(this._timeZone);
                            c.set(12, minute);
                            c.set(11, hour);
                            c.set(5, dayOfMonth);
                            c.set(2, month);
                            c.set(1, year);
                            int oldDayOfMonth = dayOfMonth;
                            int oldMonth = month;
                            int oldYear = year;
                            dayOfMonth = c.get(5);
                            month = c.get(2);
                            year = c.get(1);
                            if (month != oldMonth || dayOfMonth != oldDayOfMonth || year != oldYear) {
                                do {
                                    dayOfMonth = oldDayOfMonth - 1;
                                    month = oldMonth;
                                    year = oldYear;
                                    c = new GregorianCalendar();
                                    c.setTimeZone(this._timeZone);
                                    c.set(12, minute);
                                    c.set(11, hour);
                                    c.set(5, dayOfMonth);
                                    c.set(2, month);
                                    c.set(1, year);
                                    oldDayOfMonth = dayOfMonth;
                                    oldMonth = month;
                                    oldYear = year;
                                    dayOfMonth = c.get(5);
                                    month = c.get(2);
                                    year = c.get(1);
                                } while (month != oldMonth || dayOfMonth != oldDayOfMonth || year != oldYear);
                            }
                            else {
                                final int dayOfWeek = c.get(7);
                                if (dayOfWeekMatcher.match(dayOfWeek - 1)) {
                                    break;
                                }
                                --dayOfMonth;
                                hour = 23;
                                minute = 59;
                                if (dayOfMonth >= 1) {
                                    continue;
                                }
                                dayOfMonth = 31;
                                if (--month >= 0) {
                                    continue;
                                }
                                month = 11;
                                --year;
                            }
                        }
                        else {
                            --month;
                            dayOfMonth = 31;
                            hour = 23;
                            minute = 59;
                        }
                    }
                    else {
                        --hour;
                        minute = 59;
                    }
                }
                else {
                    if (--minute >= 0) {
                        continue;
                    }
                    minute = 59;
                    --hour;
                }
            }
            times[k] = c.getTimeInMillis() / 60000L * 1000L * 60L;
        }
        long min = Long.MAX_VALUE;
        for (int i = 0; i < size; ++i) {
            if (times[i] < min) {
                min = times[i];
            }
        }
        return this._time = min;
    }
    
    public synchronized Date prevMatchingDate() {
        return new Date(this.prevMatchingTime());
    }
}
