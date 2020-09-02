// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html;

public class PageResult
{
    private final int _pages;
    private final StringBuilder _pagerTemplate;
    private final StringBuilder _bodyTemplate;
    
    public PageResult(final int pages, final StringBuilder pagerTemplate, final StringBuilder bodyTemplate) {
        this._pages = pages;
        this._pagerTemplate = pagerTemplate;
        this._bodyTemplate = bodyTemplate;
    }
    
    public int getPages() {
        return this._pages;
    }
    
    public StringBuilder getPagerTemplate() {
        return this._pagerTemplate;
    }
    
    public StringBuilder getBodyTemplate() {
        return this._bodyTemplate;
    }
}
