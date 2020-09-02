// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.GregorianCalendar;
import java.util.Date;
import java.util.TimeZone;

public class Predictor
{
    private final SchedulingPattern schedulingPattern;
    private long time;
    private TimeZone timeZone;
    
    public Predictor(final String schedulingPattern, final long start) throws InvalidPatternException {
        this.timeZone = TimeZone.getDefault();
        this.schedulingPattern = new SchedulingPattern(schedulingPattern);
        this.time = start / 60000L * 1000L * 60L;
    }
    
    public Predictor(final String schedulingPattern, final Date start) throws InvalidPatternException {
        this(schedulingPattern, start.getTime());
    }
    
    public Predictor(final String schedulingPattern) throws InvalidPatternException {
        this(schedulingPattern, System.currentTimeMillis());
    }
    
    public Predictor(final SchedulingPattern schedulingPattern, final long start) {
        this.timeZone = TimeZone.getDefault();
        this.schedulingPattern = schedulingPattern;
        this.time = start / 60000L * 1000L * 60L;
    }
    
    public Predictor(final SchedulingPattern schedulingPattern, final Date start) {
        this(schedulingPattern, start.getTime());
    }
    
    public Predictor(final SchedulingPattern schedulingPattern) {
        this(schedulingPattern, System.currentTimeMillis());
    }
    
    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
    }
    
    public synchronized long nextMatchingTime() {
        this.time += 60000L;
        if (this.schedulingPattern.match(this.time)) {
            return this.time;
        }
        final int size = this.schedulingPattern.matcherSize;
        final long[] times = new long[size];
        for (int k = 0; k < size; ++k) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(this.time);
            c.setTimeZone(this.timeZone);
            int minute = c.get(12);
            int hour = c.get(11);
            int dayOfMonth = c.get(5);
            int month = c.get(2);
            int year = c.get(1);
            final ValueMatcher minuteMatcher = this.schedulingPattern.minuteMatchers.get(k);
            final ValueMatcher hourMatcher = this.schedulingPattern.hourMatchers.get(k);
            final ValueMatcher dayOfMonthMatcher = this.schedulingPattern.dayOfMonthMatchers.get(k);
            final ValueMatcher dayOfWeekMatcher = this.schedulingPattern.dayOfWeekMatchers.get(k);
            final ValueMatcher monthMatcher = this.schedulingPattern.monthMatchers.get(k);
            while (true) {
                if (minuteMatcher.match(minute)) {
                    if (hour > 23) {
                        hour = 0;
                        ++dayOfMonth;
                    }
                    if (hourMatcher.match(hour)) {
                        if (dayOfMonth > 31) {
                            dayOfMonth = 1;
                            ++month;
                        }
                        if (month > 11) {
                            month = 0;
                            ++year;
                        }
                        if (dayOfMonthMatcher instanceof DayOfMonthValueMatcher) {
                            final DayOfMonthValueMatcher aux = (DayOfMonthValueMatcher)dayOfMonthMatcher;
                            if (!aux.match(dayOfMonth, month + 1, c.isLeapYear(year))) {
                                ++dayOfMonth;
                                hour = 0;
                                minute = 0;
                                continue;
                            }
                        }
                        else if (!dayOfMonthMatcher.match(dayOfMonth)) {
                            ++dayOfMonth;
                            hour = 0;
                            minute = 0;
                            continue;
                        }
                        if (monthMatcher.match(month + 1)) {
                            c = new GregorianCalendar();
                            c.setTimeZone(this.timeZone);
                            c.set(12, minute);
                            c.set(11, hour);
                            c.set(5, dayOfMonth);
                            c.set(2, month);
                            c.set(1, year);
                            final int oldDayOfMonth = dayOfMonth;
                            final int oldMonth = month;
                            final int oldYear = year;
                            dayOfMonth = c.get(5);
                            month = c.get(2);
                            year = c.get(1);
                            if (month != oldMonth || dayOfMonth != oldDayOfMonth) {
                                continue;
                            }
                            if (year != oldYear) {
                                continue;
                            }
                            final int dayOfWeek = c.get(7);
                            if (dayOfWeekMatcher.match(dayOfWeek - 1)) {
                                break;
                            }
                            ++dayOfMonth;
                            hour = 0;
                            minute = 0;
                            if (dayOfMonth <= 31) {
                                continue;
                            }
                            dayOfMonth = 1;
                            if (++month <= 11) {
                                continue;
                            }
                            month = 0;
                            ++year;
                        }
                        else {
                            ++month;
                            dayOfMonth = 1;
                            hour = 0;
                            minute = 0;
                        }
                    }
                    else {
                        ++hour;
                        minute = 0;
                    }
                }
                else {
                    if (++minute <= 59) {
                        continue;
                    }
                    minute = 0;
                    ++hour;
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
        return this.time = min;
    }
    
    public synchronized Date nextMatchingDate() {
        return new Date(this.nextMatchingTime());
    }
}
