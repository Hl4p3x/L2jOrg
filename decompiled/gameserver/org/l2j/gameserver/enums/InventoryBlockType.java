// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum InventoryBlockType
{
    NONE(-1), 
    BLACKLIST(0), 
    WHITELIST(1);
    
    private int _clientId;
    
    private InventoryBlockType(final int clientId) {
        this._clientId = clientId;
    }
    
    public int getClientId() {
        return this._clientId;
    }
}
