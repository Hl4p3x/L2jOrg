// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum ClanHallType
{
    AUCTIONABLE(0), 
    SIEGEABLE(1), 
    OTHER(2);
    
    private final int _clientVal;
    
    private ClanHallType(final int clientVal) {
        this._clientVal = clientVal;
    }
    
    public int getClientVal() {
        return this._clientVal;
    }
}
