// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum Team
{
    NONE(0), 
    BLUE(1), 
    RED(2);
    
    private int _id;
    
    private Team(final int id) {
        this._id = id;
    }
    
    public int getId() {
        return this._id;
    }
}
