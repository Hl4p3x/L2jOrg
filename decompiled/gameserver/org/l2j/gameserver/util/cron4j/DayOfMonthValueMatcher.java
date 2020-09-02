// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.ArrayList;

class DayOfMonthValueMatcher extends IntArrayValueMatcher
{
    private static final int[] lastDays;
    
    public DayOfMonthValueMatcher(final ArrayList<?> values) {
        super(values);
    }
    
    public boolean match(final int value, final int month, final boolean isLeapYear) {
        return super.match(value) || (value > 27 && this.match(32) && this.isLastDayOfMonth(value, month, isLeapYear));
    }
    
    public boolean isLastDayOfMonth(final int value, final int month, final boolean isLeapYear) {
        if (isLeapYear && month == 2) {
            return value == 29;
        }
        return value == DayOfMonthValueMatcher.lastDays[month - 1];
    }
    
    static {
        lastDays = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    }
}
