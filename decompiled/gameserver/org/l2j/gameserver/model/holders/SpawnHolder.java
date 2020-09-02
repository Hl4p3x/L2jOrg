// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.Location;

public final class SpawnHolder extends Location
{
    private final int _npcId;
    private final int _respawnDelay;
    private final boolean _spawnAnimation;
    
    public SpawnHolder(final int npcId, final int x, final int y, final int z, final int heading, final boolean spawnAnimation) {
        super(x, y, z, heading);
        this._npcId = npcId;
        this._respawnDelay = 0;
        this._spawnAnimation = spawnAnimation;
    }
    
    public SpawnHolder(final int npcId, final int x, final int y, final int z, final int heading, final int respawn, final boolean spawnAnimation) {
        super(x, y, z, heading);
        this._npcId = npcId;
        this._respawnDelay = respawn;
        this._spawnAnimation = spawnAnimation;
    }
    
    public SpawnHolder(final int npcId, final Location loc, final boolean spawnAnimation) {
        super(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading());
        this._npcId = npcId;
        this._respawnDelay = 0;
        this._spawnAnimation = spawnAnimation;
    }
    
    public SpawnHolder(final int npcId, final Location loc, final int respawn, final boolean spawnAnimation) {
        super(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading());
        this._npcId = npcId;
        this._respawnDelay = respawn;
        this._spawnAnimation = spawnAnimation;
    }
    
    public final int getNpcId() {
        return this._npcId;
    }
    
    public final boolean isSpawnAnimation() {
        return this._spawnAnimation;
    }
    
    public int getRespawnDelay() {
        return this._respawnDelay;
    }
}
