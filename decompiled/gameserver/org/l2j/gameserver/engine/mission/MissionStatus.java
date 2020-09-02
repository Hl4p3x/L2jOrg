// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.mission;

public enum MissionStatus
{
    AVAILABLE(1), 
    NOT_AVAILABLE(2), 
    COMPLETED(3);
    
    private int _clientId;
    
    private MissionStatus(final int clientId) {
        this._clientId = clientId;
    }
    
    public static MissionStatus valueOf(final int clientId) {
        for (final MissionStatus type : values()) {
            if (type.getClientId() == clientId) {
                return type;
            }
        }
        return null;
    }
    
    public int getClientId() {
        return this._clientId;
    }
}
