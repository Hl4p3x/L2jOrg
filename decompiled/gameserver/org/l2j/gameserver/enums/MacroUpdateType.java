// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum MacroUpdateType
{
    ADD(1), 
    LIST(1), 
    MODIFY(2), 
    DELETE(0);
    
    private final int _id;
    
    private MacroUpdateType(final int id) {
        this._id = id;
    }
    
    public int getId() {
        return this._id;
    }
}
