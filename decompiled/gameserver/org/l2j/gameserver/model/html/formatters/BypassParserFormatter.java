// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html.formatters;

import org.l2j.gameserver.model.html.IBypassFormatter;

public class BypassParserFormatter implements IBypassFormatter
{
    public static final BypassParserFormatter INSTANCE;
    
    @Override
    public String formatBypass(final String bypass, final int page) {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, bypass, page);
    }
    
    static {
        INSTANCE = new BypassParserFormatter();
    }
}
