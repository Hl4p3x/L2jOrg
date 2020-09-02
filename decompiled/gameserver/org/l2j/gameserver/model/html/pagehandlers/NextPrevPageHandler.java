// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html.pagehandlers;

import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.IBypassFormatter;
import org.l2j.gameserver.model.html.IPageHandler;

public class NextPrevPageHandler implements IPageHandler
{
    public static final NextPrevPageHandler INSTANCE;
    
    @Override
    public void apply(final String bypass, final int currentPage, final int pages, final StringBuilder sb, final IBypassFormatter bypassFormatter, final IHtmlStyle style) {
        sb.append(style.applyBypass(bypassFormatter.formatBypass(bypass, 0), "<<", currentPage - 1 < 0));
        sb.append(style.applySeparator());
        sb.append(style.applyBypass(bypassFormatter.formatBypass(bypass, currentPage - 1), "<", currentPage <= 0));
        sb.append(style.applySeparator());
        sb.append(String.format("<td align=\"center\">Page: %d/%d</td>", currentPage + 1, pages + 1));
        sb.append(style.applySeparator());
        sb.append(style.applyBypass(bypassFormatter.formatBypass(bypass, currentPage + 1), ">", currentPage >= pages));
        sb.append(style.applySeparator());
        sb.append(style.applyBypass(bypassFormatter.formatBypass(bypass, pages), ">>", currentPage + 1 > pages));
    }
    
    static {
        INSTANCE = new NextPrevPageHandler();
    }
}
