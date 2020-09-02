// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum PlayerAction
{
    ADMIN_COMMAND, 
    ADMIN_POINT_PICKING, 
    ADMIN_SHOW_TERRITORY, 
    MERCENARY_CONFIRM;
    
    private final int _mask;
    
    private PlayerAction() {
        this._mask = 1 << this.ordinal();
    }
    
    public int getMask() {
        return this._mask;
    }
}
