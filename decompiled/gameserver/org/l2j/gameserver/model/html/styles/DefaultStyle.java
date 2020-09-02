// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html.styles;

import org.l2j.gameserver.model.html.IHtmlStyle;

public class DefaultStyle implements IHtmlStyle
{
    public static final DefaultStyle INSTANCE;
    private static final String DEFAULT_PAGE_LINK_FORMAT = "<td><a action=\"%s\">%s</a></td>";
    private static final String DEFAULT_PAGE_TEXT_FORMAT = "<td>%s</td>";
    private static final String DEFAULT_PAGER_SEPARATOR = "<td align=center> | </td>";
    
    @Override
    public String applyBypass(final String bypass, final String name, final boolean isActive) {
        if (isActive) {
            return String.format("<td>%s</td>", name);
        }
        return String.format("<td><a action=\"%s\">%s</a></td>", bypass, name);
    }
    
    @Override
    public String applySeparator() {
        return "<td align=center> | </td>";
    }
    
    static {
        INSTANCE = new DefaultStyle();
    }
}
