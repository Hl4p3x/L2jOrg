// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html;

@FunctionalInterface
public interface IPageHandler
{
    void apply(final String bypass, final int currentPage, final int pages, final StringBuilder sb, final IBypassFormatter bypassFormatter, final IHtmlStyle style);
}
