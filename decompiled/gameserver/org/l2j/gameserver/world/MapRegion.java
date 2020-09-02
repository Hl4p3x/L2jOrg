// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import org.l2j.gameserver.model.Location;
import java.util.List;
import org.l2j.gameserver.enums.Race;
import java.util.Map;

public class MapRegion
{
    private final String town;
    private final int locId;
    private final int bbs;
    private final Map<Race, String> bannedRaces;
    private List<Tile> tiles;
    private List<Location> spawnLocs;
    private List<Location> chaoticSpawnLocs;
    
    public MapRegion(final String town, final int loc, final int bbs) {
        this.bannedRaces = new HashMap<Race, String>();
        this.tiles = null;
        this.spawnLocs = null;
        this.chaoticSpawnLocs = null;
        this.town = town;
        this.locId = loc;
        this.bbs = bbs;
    }
    
    final void addMapTile(final byte x, final byte y) {
        if (this.tiles == null) {
            this.tiles = new ArrayList<Tile>();
        }
        this.tiles.add(new Tile(x, y));
    }
    
    final boolean isZoneInRegion(final int x, final int y) {
        return !Objects.isNull(this.tiles) && this.tiles.stream().anyMatch(tile -> tile.isSame(x, y));
    }
    
    public final void addSpawn(final int x, final int y, final int z) {
        if (Objects.isNull(this.spawnLocs)) {
            this.spawnLocs = new ArrayList<Location>();
        }
        this.spawnLocs.add(new Location(x, y, z));
    }
    
    final void addChaoticSpawn(final int x, final int y, final int z) {
        if (Objects.isNull(this.chaoticSpawnLocs)) {
            this.chaoticSpawnLocs = new ArrayList<Location>();
        }
        this.chaoticSpawnLocs.add(new Location(x, y, z));
    }
    
    public final Location getSpawnLoc() {
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return (Location)Rnd.get((List)this.spawnLocs);
        }
        return this.spawnLocs.get(0);
    }
    
    final Location getChaoticSpawnLoc() {
        if (!Objects.nonNull(this.chaoticSpawnLocs)) {
            return this.getSpawnLoc();
        }
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return (Location)Rnd.get((List)this.chaoticSpawnLocs);
        }
        return this.chaoticSpawnLocs.get(0);
    }
    
    final void addBannedRace(final String race, final String point) {
        this.bannedRaces.put(Race.valueOf(race), point);
    }
    
    final Map<Race, String> getBannedRaces() {
        return this.bannedRaces;
    }
    
    public final String getTown() {
        return this.town;
    }
    
    public final int getLocId() {
        return this.locId;
    }
    
    public final int getBbs() {
        return this.bbs;
    }
    
    private static class Tile
    {
        private byte x;
        private byte y;
        
        private Tile(final byte x, final byte y) {
            this.x = x;
            this.y = y;
        }
        
        private boolean isSame(final int x, final int y) {
            return this.x == x && this.y == y;
        }
    }
}
