// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.interfaces;

import org.l2j.gameserver.world.zone.type.BannedSpawnTerritory;
import java.util.List;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;

public interface ITerritorized
{
    void addTerritory(final SpawnTerritory territory);
    
    List<SpawnTerritory> getTerritories();
    
    void addBannedTerritory(final BannedSpawnTerritory territory);
    
    List<BannedSpawnTerritory> getBannedTerritories();
}
