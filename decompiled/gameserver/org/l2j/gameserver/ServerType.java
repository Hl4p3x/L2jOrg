// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import org.l2j.commons.util.Util;

public enum ServerType
{
    NORMAL, 
    RELAX, 
    TEST, 
    BROAD, 
    RESTRICTED, 
    EVENT, 
    FREE, 
    UNK_7, 
    WORLD, 
    NEW, 
    CLASSIC, 
    ARENA, 
    BLOODY;
    
    private int mask;
    
    private ServerType() {
        this.mask = 1 << this.ordinal();
    }
    
    public int getMask() {
        return this.mask;
    }
    
    public static int maskOf(final String... types) {
        int type = 0;
        for (final String t : types) {
            if (!Util.isNullOrEmpty((CharSequence)t)) {
                try {
                    type |= valueOf(t.trim().toUpperCase()).mask;
                }
                catch (Exception ex) {}
            }
        }
        return type;
    }
}
