// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

public final class FloodProtectorConfig
{
    public String FLOOD_PROTECTOR_TYPE;
    public int FLOOD_PROTECTION_INTERVAL;
    public boolean LOG_FLOODING;
    public int PUNISHMENT_LIMIT;
    public String PUNISHMENT_TYPE;
    public long PUNISHMENT_TIME;
    
    public FloodProtectorConfig(final String floodProtectorType) {
        this.FLOOD_PROTECTOR_TYPE = floodProtectorType;
    }
}
