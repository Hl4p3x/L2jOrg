// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.world.zone.ZoneArea;

public class SpawnTerritory
{
    private final String name;
    private final ZoneArea territory;
    
    public SpawnTerritory(final String name, final ZoneArea territory) {
        this.name = name;
        this.territory = territory;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Location getRandomPoint() {
        return this.territory.getRandomPoint();
    }
    
    public boolean isInsideZone(final int x, final int y, final int z) {
        return this.territory.isInsideZone(x, y, z);
    }
    
    public void visualizeZone(final int z) {
        this.territory.visualizeZone(z);
    }
}
