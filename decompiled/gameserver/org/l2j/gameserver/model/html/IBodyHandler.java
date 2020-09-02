// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.html;

import java.util.Iterator;
import java.util.Collection;

@FunctionalInterface
public interface IBodyHandler<T>
{
    void apply(final int pages, final T type, final StringBuilder sb);
    
    default void create(final Collection<T> elements, final int pages, final int start, final int elementsPerPage, final StringBuilder sb) {
        int i = 0;
        for (final T element : elements) {
            if (i++ < start) {
                continue;
            }
            this.apply(pages, element, sb);
            if (i >= elementsPerPage + start) {
                break;
            }
        }
    }
}
