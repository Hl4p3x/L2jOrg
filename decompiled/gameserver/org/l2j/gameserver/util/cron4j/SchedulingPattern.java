// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;

public class SchedulingPattern
{
    private static final ValueParser MINUTE_VALUE_PARSER;
    private static final ValueParser HOUR_VALUE_PARSER;
    private static final ValueParser DAY_OF_MONTH_VALUE_PARSER;
    private static final ValueParser MONTH_VALUE_PARSER;
    private static final ValueParser DAY_OF_WEEK_VALUE_PARSER;
    private final String asString;
    protected ArrayList<ValueMatcher> minuteMatchers;
    protected ArrayList<ValueMatcher> hourMatchers;
    protected ArrayList<ValueMatcher> dayOfMonthMatchers;
    protected ArrayList<ValueMatcher> monthMatchers;
    protected ArrayList<ValueMatcher> dayOfWeekMatchers;
    protected int matcherSize;
    
    public SchedulingPattern(final String pattern) throws InvalidPatternException {
        this.minuteMatchers = new ArrayList<ValueMatcher>();
        this.hourMatchers = new ArrayList<ValueMatcher>();
        this.dayOfMonthMatchers = new ArrayList<ValueMatcher>();
        this.monthMatchers = new ArrayList<ValueMatcher>();
        this.dayOfWeekMatchers = new ArrayList<ValueMatcher>();
        this.matcherSize = 0;
        this.asString = pattern;
        final StringTokenizer st1 = new StringTokenizer(pattern, "|");
        if (st1.countTokens() < 1) {
            throw new InvalidPatternException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pattern));
        }
        while (st1.hasMoreTokens()) {
            final String localPattern = st1.nextToken();
            final StringTokenizer st2 = new StringTokenizer(localPattern, " \t");
            if (st2.countTokens() != 5) {
                throw new InvalidPatternException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, localPattern));
            }
            try {
                this.minuteMatchers.add(this.buildValueMatcher(st2.nextToken(), SchedulingPattern.MINUTE_VALUE_PARSER));
            }
            catch (Exception e) {
                throw new InvalidPatternException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, localPattern, e.getMessage()));
            }
            try {
                this.hourMatchers.add(this.buildValueMatcher(st2.nextToken(), SchedulingPattern.HOUR_VALUE_PARSER));
            }
            catch (Exception e) {
                throw new InvalidPatternException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, localPattern, e.getMessage()));
            }
            try {
                this.dayOfMonthMatchers.add(this.buildValueMatcher(st2.nextToken(), SchedulingPattern.DAY_OF_MONTH_VALUE_PARSER));
            }
            catch (Exception e) {
                throw new InvalidPatternException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, localPattern, e.getMessage()));
            }
            try {
                this.monthMatchers.add(this.buildValueMatcher(st2.nextToken(), SchedulingPattern.MONTH_VALUE_PARSER));
            }
            catch (Exception e) {
                throw new InvalidPatternException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, localPattern, e.getMessage()));
            }
            try {
                this.dayOfWeekMatchers.add(this.buildValueMatcher(st2.nextToken(), SchedulingPattern.DAY_OF_WEEK_VALUE_PARSER));
            }
            catch (Exception e) {
                throw new InvalidPatternException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, localPattern, e.getMessage()));
            }
            ++this.matcherSize;
        }
    }
    
    public static boolean validate(final String schedulingPattern) {
        try {
            new SchedulingPattern(schedulingPattern);
        }
        catch (InvalidPatternException e) {
            return false;
        }
        return true;
    }
    
    static int parseAlias(final String value, final String[] aliases, final int offset) throws Exception {
        for (int i = 0; i < aliases.length; ++i) {
            if (aliases[i].equalsIgnoreCase(value)) {
                return offset + i;
            }
        }
        throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, value));
    }
    
    private ValueMatcher buildValueMatcher(final String str, final ValueParser parser) throws Exception {
        if (str.length() == 1 && str.equals("*")) {
            return new AlwaysTrueValueMatcher();
        }
        final ArrayList<Object> values = new ArrayList<Object>();
        final StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens()) {
            final String element = st.nextToken();
            ArrayList<Integer> local;
            try {
                local = this.parseListElement(element, parser);
            }
            catch (Exception e) {
                throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, str, element, e.getMessage()));
            }
            for (final Object value : local) {
                final Integer integer = (Integer)value;
                if (!values.contains(value)) {
                    values.add(value);
                }
            }
        }
        if (values.size() == 0) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, str));
        }
        if (parser == SchedulingPattern.DAY_OF_MONTH_VALUE_PARSER) {
            return new DayOfMonthValueMatcher(values);
        }
        return new IntArrayValueMatcher(values);
    }
    
    private ArrayList<Integer> parseListElement(final String str, final ValueParser parser) throws Exception {
        final StringTokenizer st = new StringTokenizer(str, "/");
        final int size = st.countTokens();
        if (size < 1 || size > 2) {
            throw new Exception("syntax error");
        }
        ArrayList<Integer> values;
        try {
            values = this.parseRange(st.nextToken(), parser);
        }
        catch (Exception e) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
        }
        if (size != 2) {
            return values;
        }
        final String dStr = st.nextToken();
        int div;
        try {
            div = Integer.parseInt(dStr);
        }
        catch (NumberFormatException e2) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, dStr));
        }
        if (div < 1) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, div));
        }
        final ArrayList<Integer> values2 = new ArrayList<Integer>();
        for (int i = 0; i < values.size(); i += div) {
            values2.add(values.get(i));
        }
        return values2;
    }
    
    private ArrayList<Integer> parseRange(final String str, final ValueParser parser) throws Exception {
        if (str.equals("*")) {
            final int min = parser.getMinValue();
            final int max = parser.getMaxValue();
            final ArrayList<Integer> values = new ArrayList<Integer>();
            for (int i = min; i <= max; ++i) {
                values.add(i);
            }
            return values;
        }
        final StringTokenizer st = new StringTokenizer(str, "-");
        final int size = st.countTokens();
        if (size < 1 || size > 2) {
            throw new Exception("syntax error");
        }
        final String v1Str = st.nextToken();
        int v1;
        try {
            v1 = parser.parse(v1Str);
        }
        catch (Exception e) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, v1Str, e.getMessage()));
        }
        if (size == 1) {
            final ArrayList<Integer> values2 = new ArrayList<Integer>();
            values2.add(v1);
            return values2;
        }
        final String v2Str = st.nextToken();
        int v2;
        try {
            v2 = parser.parse(v2Str);
        }
        catch (Exception e2) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, v2Str, e2.getMessage()));
        }
        final ArrayList<Integer> values3 = new ArrayList<Integer>();
        if (v1 < v2) {
            for (int j = v1; j <= v2; ++j) {
                values3.add(j);
            }
        }
        else if (v1 > v2) {
            final int min2 = parser.getMinValue();
            for (int max2 = parser.getMaxValue(), k = v1; k <= max2; ++k) {
                values3.add(k);
            }
            for (int k = min2; k <= v2; ++k) {
                values3.add(k);
            }
        }
        else {
            values3.add(v1);
        }
        return values3;
    }
    
    public boolean match(final TimeZone timezone, final long millis) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(millis);
        gc.setTimeZone(timezone);
        final int minute = gc.get(12);
        final int hour = gc.get(11);
        final int dayOfMonth = gc.get(5);
        final int month = gc.get(2) + 1;
        final int dayOfWeek = gc.get(7) - 1;
        final int year = gc.get(1);
        for (int i = 0; i < this.matcherSize; ++i) {
            final ValueMatcher minuteMatcher = this.minuteMatchers.get(i);
            final ValueMatcher hourMatcher = this.hourMatchers.get(i);
            final ValueMatcher dayOfMonthMatcher = this.dayOfMonthMatchers.get(i);
            final ValueMatcher monthMatcher = this.monthMatchers.get(i);
            final ValueMatcher dayOfWeekMatcher = this.dayOfWeekMatchers.get(i);
            boolean b = false;
            Label_0256: {
                Label_0255: {
                    if (minuteMatcher.match(minute) && hourMatcher.match(hour)) {
                        if (dayOfMonthMatcher instanceof DayOfMonthValueMatcher) {
                            if (!((DayOfMonthValueMatcher)dayOfMonthMatcher).match(dayOfMonth, month, gc.isLeapYear(year))) {
                                break Label_0255;
                            }
                        }
                        else if (!dayOfMonthMatcher.match(dayOfMonth)) {
                            break Label_0255;
                        }
                        if (monthMatcher.match(month) && dayOfWeekMatcher.match(dayOfWeek)) {
                            b = true;
                            break Label_0256;
                        }
                    }
                }
                b = false;
            }
            final boolean eval = b;
            if (eval) {
                return true;
            }
        }
        return false;
    }
    
    public boolean match(final long millis) {
        return this.match(TimeZone.getDefault(), millis);
    }
    
    @Override
    public String toString() {
        return this.asString;
    }
    
    static {
        MINUTE_VALUE_PARSER = new MinuteValueParser();
        HOUR_VALUE_PARSER = new HourValueParser();
        DAY_OF_MONTH_VALUE_PARSER = new DayOfMonthValueParser();
        MONTH_VALUE_PARSER = new MonthValueParser();
        DAY_OF_WEEK_VALUE_PARSER = new DayOfWeekValueParser();
    }
    
    private static class SimpleValueParser implements ValueParser
    {
        protected int minValue;
        protected int maxValue;
        
        public SimpleValueParser(final int minValue, final int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
        
        @Override
        public int parse(final String value) throws Exception {
            int i;
            try {
                i = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                throw new Exception("invalid integer value");
            }
            if (i < this.minValue || i > this.maxValue) {
                throw new Exception("value out of range");
            }
            return i;
        }
        
        @Override
        public int getMinValue() {
            return this.minValue;
        }
        
        @Override
        public int getMaxValue() {
            return this.maxValue;
        }
    }
    
    private static class MinuteValueParser extends SimpleValueParser
    {
        public MinuteValueParser() {
            super(0, 59);
        }
    }
    
    private static class HourValueParser extends SimpleValueParser
    {
        public HourValueParser() {
            super(0, 23);
        }
    }
    
    private static class DayOfMonthValueParser extends SimpleValueParser
    {
        public DayOfMonthValueParser() {
            super(1, 31);
        }
        
        @Override
        public int parse(final String value) throws Exception {
            if (value.equalsIgnoreCase("L")) {
                return 32;
            }
            return super.parse(value);
        }
    }
    
    private static class MonthValueParser extends SimpleValueParser
    {
        private static String[] ALIASES;
        
        public MonthValueParser() {
            super(1, 12);
        }
        
        @Override
        public int parse(final String value) throws Exception {
            try {
                return super.parse(value);
            }
            catch (Exception e) {
                return SchedulingPattern.parseAlias(value, MonthValueParser.ALIASES, 1);
            }
        }
        
        static {
            MonthValueParser.ALIASES = new String[] { "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
        }
    }
    
    private static class DayOfWeekValueParser extends SimpleValueParser
    {
        private static String[] ALIASES;
        
        public DayOfWeekValueParser() {
            super(0, 7);
        }
        
        @Override
        public int parse(final String value) throws Exception {
            try {
                return super.parse(value) % 7;
            }
            catch (Exception e) {
                return SchedulingPattern.parseAlias(value, DayOfWeekValueParser.ALIASES, 0);
            }
        }
        
        static {
            DayOfWeekValueParser.ALIASES = new String[] { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };
        }
    }
    
    private interface ValueParser
    {
        int parse(final String value) throws Exception;
        
        int getMinValue();
        
        int getMaxValue();
    }
}
