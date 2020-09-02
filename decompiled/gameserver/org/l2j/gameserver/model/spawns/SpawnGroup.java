// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.spawns;

import java.util.Iterator;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Collections;
import java.util.ArrayList;
import org.l2j.gameserver.world.zone.type.BannedSpawnTerritory;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import java.util.List;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IParameterized;
import org.l2j.gameserver.model.interfaces.ITerritorized;

public class SpawnGroup implements Cloneable, ITerritorized, IParameterized<StatsSet>
{
    private final String _name;
    private final boolean _spawnByDefault;
    private final List<NpcSpawnTemplate> spawns;
    private List<SpawnTerritory> _territories;
    private List<BannedSpawnTerritory> _bannedTerritories;
    private StatsSet _parameters;
    
    public SpawnGroup(final StatsSet set) {
        this(set.getString("name", null), set.getBoolean("spawnByDefault", true));
    }
    
    private SpawnGroup(final String name, final boolean spawnByDefault) {
        this.spawns = new ArrayList<NpcSpawnTemplate>();
        this._name = name;
        this._spawnByDefault = spawnByDefault;
    }
    
    public String getName() {
        return this._name;
    }
    
    public boolean isSpawningByDefault() {
        return this._spawnByDefault;
    }
    
    public void addSpawn(final NpcSpawnTemplate template) {
        this.spawns.add(template);
    }
    
    public List<NpcSpawnTemplate> getSpawns() {
        return this.spawns;
    }
    
    @Override
    public void addTerritory(final SpawnTerritory territory) {
        if (this._territories == null) {
            this._territories = new ArrayList<SpawnTerritory>();
        }
        this._territories.add(territory);
    }
    
    @Override
    public List<SpawnTerritory> getTerritories() {
        return (this._territories != null) ? this._territories : Collections.emptyList();
    }
    
    @Override
    public void addBannedTerritory(final BannedSpawnTerritory territory) {
        if (this._bannedTerritories == null) {
            this._bannedTerritories = new ArrayList<BannedSpawnTerritory>();
        }
        this._bannedTerritories.add(territory);
    }
    
    @Override
    public List<BannedSpawnTerritory> getBannedTerritories() {
        return (this._bannedTerritories != null) ? this._bannedTerritories : Collections.emptyList();
    }
    
    @Override
    public StatsSet getParameters() {
        return this._parameters;
    }
    
    @Override
    public void setParameters(final StatsSet parameters) {
        this._parameters = parameters;
    }
    
    public void spawnAll() {
        this.spawnAll(null);
    }
    
    public void spawnAll(final Instance instance) {
        this.spawns.parallelStream().forEach(template -> template.spawn(instance));
    }
    
    public void despawnAll() {
        this.spawns.forEach(NpcSpawnTemplate::despawn);
    }
    
    public SpawnGroup clone() {
        final SpawnGroup group = new SpawnGroup(this._name, this._spawnByDefault);
        for (final BannedSpawnTerritory territory : this.getBannedTerritories()) {
            group.addBannedTerritory(territory);
        }
        for (final SpawnTerritory territory2 : this.getTerritories()) {
            group.addTerritory(territory2);
        }
        for (final NpcSpawnTemplate spawn : this.spawns) {
            group.addSpawn(spawn.clone());
        }
        return group;
    }
}
