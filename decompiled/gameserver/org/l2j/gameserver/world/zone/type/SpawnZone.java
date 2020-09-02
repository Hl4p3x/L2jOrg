// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.slf4j.LoggerFactory;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import java.util.ArrayList;
import org.l2j.gameserver.model.Location;
import java.util.List;
import org.slf4j.Logger;
import org.l2j.gameserver.world.zone.Zone;

public abstract class SpawnZone extends Zone
{
    private static final Logger LOGGER;
    private List<Location> spawnLocs;
    private List<Location> otherSpawnLocs;
    private List<Location> chaoticSpawnLocs;
    private List<Location> banishSpawnLocs;
    
    SpawnZone(final int id) {
        super(id);
        this.spawnLocs = null;
        this.otherSpawnLocs = null;
        this.chaoticSpawnLocs = null;
        this.banishSpawnLocs = null;
    }
    
    public void parseLoc(final int x, final int y, final int z, final String type) {
        if (type == null || type.isEmpty()) {
            this.addSpawn(x, y, z);
        }
        else {
            switch (type) {
                case "other": {
                    this.addOtherSpawn(x, y, z);
                    break;
                }
                case "chaotic": {
                    this.addChaoticSpawn(x, y, z);
                    break;
                }
                case "banish": {
                    this.addBanishSpawn(x, y, z);
                    break;
                }
                default: {
                    SpawnZone.LOGGER.warn("Unknown location type: {}", (Object)type);
                    break;
                }
            }
        }
    }
    
    public final void addSpawn(final int x, final int y, final int z) {
        if (this.spawnLocs == null) {
            this.spawnLocs = new ArrayList<Location>();
        }
        this.spawnLocs.add(new Location(x, y, z));
    }
    
    private void addOtherSpawn(final int x, final int y, final int z) {
        if (this.otherSpawnLocs == null) {
            this.otherSpawnLocs = new ArrayList<Location>();
        }
        this.otherSpawnLocs.add(new Location(x, y, z));
    }
    
    private void addChaoticSpawn(final int x, final int y, final int z) {
        if (this.chaoticSpawnLocs == null) {
            this.chaoticSpawnLocs = new ArrayList<Location>();
        }
        this.chaoticSpawnLocs.add(new Location(x, y, z));
    }
    
    private void addBanishSpawn(final int x, final int y, final int z) {
        if (this.banishSpawnLocs == null) {
            this.banishSpawnLocs = new ArrayList<Location>();
        }
        this.banishSpawnLocs.add(new Location(x, y, z));
    }
    
    public final List<Location> getSpawns() {
        return this.spawnLocs;
    }
    
    public final Location getSpawnLoc() {
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return this.spawnLocs.get(Rnd.get(this.spawnLocs.size()));
        }
        return this.spawnLocs.get(0);
    }
    
    public final Location getOtherSpawnLoc() {
        if (this.otherSpawnLocs == null) {
            return this.getSpawnLoc();
        }
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return this.otherSpawnLocs.get(Rnd.get(this.otherSpawnLocs.size()));
        }
        return this.otherSpawnLocs.get(0);
    }
    
    public final Location getChaoticSpawnLoc() {
        if (this.chaoticSpawnLocs == null) {
            return this.getSpawnLoc();
        }
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return this.chaoticSpawnLocs.get(Rnd.get(this.chaoticSpawnLocs.size()));
        }
        return this.chaoticSpawnLocs.get(0);
    }
    
    public Location getBanishSpawnLoc() {
        if (this.banishSpawnLocs == null) {
            return this.getSpawnLoc();
        }
        if (Config.RANDOM_RESPAWN_IN_TOWN_ENABLED) {
            return this.banishSpawnLocs.get(Rnd.get(this.banishSpawnLocs.size()));
        }
        return this.banishSpawnLocs.get(0);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SpawnZone.class);
    }
}
