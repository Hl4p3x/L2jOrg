// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import java.util.regex.Matcher;
import java.util.Map;
import java.util.function.Supplier;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import org.l2j.gameserver.model.StatsSet;

public class BypassParser extends StatsSet
{
    private static final String ALLOWED_CHARS = "a-zA-Z0-9-_`!@#%^&*()\\[\\]|\\\\/";
    private static final Pattern PATTERN;
    
    public BypassParser(final String bypass) {
        super((Supplier<Map<String, Object>>)LinkedHashMap::new);
        this.process(bypass);
    }
    
    private void process(final String bypass) {
        final Matcher regexMatcher = BypassParser.PATTERN.matcher(bypass);
        while (regexMatcher.find()) {
            final String name = regexMatcher.group(1);
            final String escapedValue = regexMatcher.group(2).trim();
            final String unescapedValue = regexMatcher.group(3);
            this.set(name, (unescapedValue != null) ? unescapedValue.trim() : escapedValue);
        }
    }
    
    static {
        PATTERN = Pattern.compile(String.format("([%s]*)=('([%s ]*)'|[%s]*)", "a-zA-Z0-9-_`!@#%^&*()\\[\\]|\\\\/", "a-zA-Z0-9-_`!@#%^&*()\\[\\]|\\\\/", "a-zA-Z0-9-_`!@#%^&*()\\[\\]|\\\\/"));
    }
}
