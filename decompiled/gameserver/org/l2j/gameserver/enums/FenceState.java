// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum FenceState
{
    HIDDEN(0), 
    OPENED(1), 
    CLOSED(2), 
    CLOSED_HIDDEN(0);
    
    final int _clientId;
    
    private FenceState(final int clientId) {
        this._clientId = clientId;
    }
    
    public int getClientId() {
        return this._clientId;
    }
    
    public boolean isGeodataEnabled() {
        return this == FenceState.CLOSED_HIDDEN || this == FenceState.CLOSED;
    }
}
