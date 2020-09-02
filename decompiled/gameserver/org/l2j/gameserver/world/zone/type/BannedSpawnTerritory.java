// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.world.zone.ZoneArea;

public class BannedSpawnTerritory
{
    private final String name;
    private final ZoneArea area;
    
    public BannedSpawnTerritory(final String name, final ZoneArea area) {
        this.name = name;
        this.area = area;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isInsideZone(final int x, final int y, final int z) {
        return this.area.isInsideZone(x, y, z);
    }
}
