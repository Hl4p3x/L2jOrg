// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum LuckyGameItemType
{
    COMMON(1), 
    UNIQUE(2), 
    RARE(3);
    
    private final int _clientId;
    
    private LuckyGameItemType(final int clientId) {
        this._clientId = clientId;
    }
    
    public int getClientId() {
        return this._clientId;
    }
}
