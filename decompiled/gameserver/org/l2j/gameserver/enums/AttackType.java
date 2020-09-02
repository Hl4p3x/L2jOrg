// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum AttackType
{
    MISSED(1), 
    BLOCKED(2), 
    CRITICAL(4), 
    SHOT_USED(8);
    
    private final int _mask;
    
    private AttackType(final int mask) {
        this._mask = mask;
    }
    
    public int getMask() {
        return this._mask;
    }
}
