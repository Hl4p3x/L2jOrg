// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum ShotType
{
    SOULSHOTS(0), 
    SPIRITSHOTS(1), 
    BEAST_SOULSHOTS(2), 
    BEAST_SPIRITSHOTS(3);
    
    private static final ShotType[] CACHED;
    private final int clientType;
    
    private ShotType(final int clientType) {
        this.clientType = clientType;
    }
    
    public static ShotType of(final int type) {
        for (final ShotType shotType : ShotType.CACHED) {
            if (shotType.clientType == type) {
                return shotType;
            }
        }
        return null;
    }
    
    public int getClientType() {
        return this.clientType;
    }
    
    static {
        CACHED = values();
    }
}
