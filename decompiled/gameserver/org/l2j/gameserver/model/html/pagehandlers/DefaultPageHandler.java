// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html.pagehandlers;

import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.IBypassFormatter;
import org.l2j.gameserver.model.html.IPageHandler;

public class DefaultPageHandler implements IPageHandler
{
    public static final DefaultPageHandler INSTANCE;
    protected final int pagesOffset;
    
    public DefaultPageHandler(final int pagesOffset) {
        this.pagesOffset = pagesOffset;
    }
    
    @Override
    public void apply(final String bypass, final int currentPage, final int pages, final StringBuilder sb, final IBypassFormatter formatter, final IHtmlStyle style) {
        final int pagerStart = Math.max(currentPage - this.pagesOffset, 0);
        final int pagerFinish = Math.min(currentPage + this.pagesOffset + 1, pages);
        if (pagerStart > this.pagesOffset) {
            for (int i = 0; i < this.pagesOffset; ++i) {
                sb.append(style.applyBypass(formatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
            }
            sb.append(style.applySeparator());
        }
        for (int i = pagerStart; i < pagerFinish; ++i) {
            sb.append(style.applyBypass(formatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
        }
        if (pages > pagerFinish) {
            sb.append(style.applySeparator());
            for (int i = pages - this.pagesOffset; i < pages; ++i) {
                sb.append(style.applyBypass(formatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
            }
        }
    }
    
    static {
        INSTANCE = new DefaultPageHandler(2);
    }
}
