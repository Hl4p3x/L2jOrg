// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html;

import java.util.Objects;
import java.util.Arrays;
import org.l2j.gameserver.model.html.styles.DefaultStyle;
import org.l2j.gameserver.model.html.formatters.DefaultFormatter;
import org.l2j.gameserver.model.html.pagehandlers.DefaultPageHandler;
import java.util.Collection;

public class PageBuilder<T>
{
    private final Collection<T> elements;
    private final int elementsPerPage;
    private final String bypass;
    private int currentPage;
    private IPageHandler pageHandler;
    private IBypassFormatter formatter;
    private IHtmlStyle style;
    private IBodyHandler<T> bodyHandler;
    
    private PageBuilder(final Collection<T> elements, final int elementsPerPage, final String bypass) {
        this.currentPage = 0;
        this.pageHandler = DefaultPageHandler.INSTANCE;
        this.formatter = DefaultFormatter.INSTANCE;
        this.style = DefaultStyle.INSTANCE;
        this.elements = elements;
        this.elementsPerPage = elementsPerPage;
        this.bypass = bypass;
    }
    
    public static <T> PageBuilder<T> newBuilder(final Collection<T> elements, final int elementsPerPage, final String bypass) {
        return new PageBuilder<T>(elements, elementsPerPage, bypass.trim());
    }
    
    public static <T> PageBuilder<T> newBuilder(final T[] elements, final int elementsPerPage, final String bypass) {
        return new PageBuilder<T>(Arrays.asList(elements), elementsPerPage, bypass.trim());
    }
    
    public PageBuilder<T> currentPage(final int currentPage) {
        this.currentPage = Math.max(currentPage, 0);
        return this;
    }
    
    public PageBuilder<T> bodyHandler(final IBodyHandler<T> bodyHandler) {
        Objects.requireNonNull(bodyHandler, "Body Handler cannot be null!");
        this.bodyHandler = bodyHandler;
        return this;
    }
    
    public PageBuilder<T> pageHandler(final IPageHandler pageHandler) {
        Objects.requireNonNull(pageHandler, "Page Handler cannot be null!");
        this.pageHandler = pageHandler;
        return this;
    }
    
    public PageBuilder<T> formatter(final IBypassFormatter formatter) {
        Objects.requireNonNull(formatter, "Formatter cannot be null!");
        this.formatter = formatter;
        return this;
    }
    
    public PageBuilder<T> style(final IHtmlStyle style) {
        Objects.requireNonNull(style, "Style cannot be null!");
        this.style = style;
        return this;
    }
    
    public PageResult build() {
        Objects.requireNonNull(this.bodyHandler, "Body was not set!");
        final int pages = this.elements.size() / this.elementsPerPage + ((this.elements.size() % this.elementsPerPage > 0) ? 1 : 0);
        final StringBuilder pagerTemplate = new StringBuilder();
        if (pages > 1) {
            this.pageHandler.apply(this.bypass, this.currentPage, pages, pagerTemplate, this.formatter, this.style);
        }
        if (this.currentPage > pages) {
            this.currentPage = pages - 1;
        }
        final int start = Math.max(this.elementsPerPage * this.currentPage, 0);
        final StringBuilder sb = new StringBuilder();
        this.bodyHandler.create(this.elements, pages, start, this.elementsPerPage, sb);
        return new PageResult(pages, pagerTemplate, sb);
    }
}
