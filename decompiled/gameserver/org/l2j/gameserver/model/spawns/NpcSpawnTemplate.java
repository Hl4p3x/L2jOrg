// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.spawns;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.world.zone.type.BannedSpawnTerritory;
import java.util.Iterator;
import java.util.Objects;
import org.l2j.commons.util.Rnd;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.Location;
import java.util.Collections;
import org.l2j.gameserver.world.zone.ZoneManager;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.world.zone.type.SpawnTerritory;
import org.l2j.gameserver.model.ChanceLocation;
import java.util.List;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Set;
import java.time.Duration;
import org.slf4j.Logger;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IParameterized;

public class NpcSpawnTemplate implements Cloneable, IParameterized<StatsSet>
{
    private static final Logger LOGGER;
    private final int id;
    private final int _count;
    private final Duration respawnTime;
    private final Duration respawnTimeRandom;
    private final boolean _spawnAnimation;
    private final boolean saveInDB;
    private final String _dbName;
    private final SpawnTemplate spawnTemplate;
    private final SpawnGroup group;
    private final Set<Npc> _spawnedNpcs;
    private List<ChanceLocation> locations;
    private SpawnTerritory zone;
    private StatsSet _parameters;
    private List<MinionHolder> _minions;
    
    private NpcSpawnTemplate(final NpcSpawnTemplate template) {
        this._spawnedNpcs = (Set<Npc>)ConcurrentHashMap.newKeySet();
        this.spawnTemplate = template.spawnTemplate;
        this.group = template.group;
        this.id = template.id;
        this._count = template._count;
        this.respawnTime = template.respawnTime;
        this.respawnTimeRandom = template.respawnTimeRandom;
        this._spawnAnimation = template._spawnAnimation;
        this.saveInDB = template.saveInDB;
        this._dbName = template._dbName;
        this.locations = template.locations;
        this.zone = template.zone;
        this._parameters = template._parameters;
        this._minions = template._minions;
    }
    
    public NpcSpawnTemplate(final SpawnTemplate spawnTemplate, final SpawnGroup group, final StatsSet set) {
        this._spawnedNpcs = (Set<Npc>)ConcurrentHashMap.newKeySet();
        this.spawnTemplate = spawnTemplate;
        this.group = group;
        this.id = set.getInt("id");
        this._count = set.getInt("count", 1);
        this.respawnTime = set.getDuration("respawnTime", null);
        this.respawnTimeRandom = set.getDuration("respawnRandom", null);
        this._spawnAnimation = set.getBoolean("spawnAnimation", false);
        this.saveInDB = set.getBoolean("dbSave", false);
        this._dbName = set.getString("dbName", null);
        this._parameters = this.mergeParameters(spawnTemplate, group);
        final int x = set.getInt("x", Integer.MAX_VALUE);
        final int y = set.getInt("y", Integer.MAX_VALUE);
        final int z = set.getInt("z", Integer.MAX_VALUE);
        final boolean xDefined = x != Integer.MAX_VALUE;
        final boolean yDefined = y != Integer.MAX_VALUE;
        final boolean zDefined = z != Integer.MAX_VALUE;
        if (xDefined && yDefined && zDefined) {
            (this.locations = new ArrayList<ChanceLocation>()).add(new ChanceLocation(x, y, z, set.getInt("heading", 0), 100.0));
        }
        else {
            if (xDefined || yDefined || zDefined) {
                throw new IllegalStateException(String.format("Spawn with partially declared and x: %s y: %s z: %s!", this.processParam(x), this.processParam(y), this.processParam(z)));
            }
            final String zoneName = set.getString("zone", null);
            if (zoneName != null) {
                final SpawnTerritory zone = ZoneManager.getInstance().getSpawnTerritory(zoneName);
                if (zone == null) {
                    throw new NullPointerException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, zoneName));
                }
                this.zone = zone;
            }
        }
        this.mergeParameters(spawnTemplate, group);
    }
    
    private StatsSet mergeParameters(final SpawnTemplate spawnTemplate, final SpawnGroup group) {
        if (this._parameters == null && spawnTemplate.getParameters() == null && group.getParameters() == null) {
            return null;
        }
        final StatsSet set = new StatsSet();
        if (spawnTemplate.getParameters() != null) {
            set.merge(spawnTemplate.getParameters());
        }
        if (group.getParameters() != null) {
            set.merge(group.getParameters());
        }
        if (this._parameters != null) {
            set.merge(this._parameters);
        }
        return set;
    }
    
    public void addSpawnLocation(final ChanceLocation loc) {
        if (this.locations == null) {
            this.locations = new ArrayList<ChanceLocation>();
        }
        this.locations.add(loc);
    }
    
    public SpawnTemplate getSpawnTemplate() {
        return this.spawnTemplate;
    }
    
    public SpawnGroup getGroup() {
        return this.group;
    }
    
    private String processParam(final int value) {
        return (value != Integer.MAX_VALUE) ? Integer.toString(value) : "undefined";
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getCount() {
        return this._count;
    }
    
    public Duration getRespawnTime() {
        return this.respawnTime;
    }
    
    public Duration getRespawnTimeRandom() {
        return this.respawnTimeRandom;
    }
    
    public List<ChanceLocation> getLocation() {
        return this.locations;
    }
    
    public SpawnTerritory getZone() {
        return this.zone;
    }
    
    @Override
    public StatsSet getParameters() {
        return this._parameters;
    }
    
    @Override
    public void setParameters(final StatsSet parameters) {
        if (this._parameters == null) {
            this._parameters = parameters;
        }
        else {
            this._parameters.merge(parameters);
        }
    }
    
    public boolean hasSpawnAnimation() {
        return this._spawnAnimation;
    }
    
    public boolean hasDBSave() {
        return this.saveInDB;
    }
    
    public String getDBName() {
        return this._dbName;
    }
    
    public List<MinionHolder> getMinions() {
        return (this._minions != null) ? this._minions : Collections.emptyList();
    }
    
    public void addMinion(final MinionHolder minion) {
        if (this._minions == null) {
            this._minions = new ArrayList<MinionHolder>();
        }
        this._minions.add(minion);
    }
    
    public Set<Npc> getSpawnedNpcs() {
        return this._spawnedNpcs;
    }
    
    public final Location getSpawnLocation() {
        if (!Util.isNullOrEmpty((Collection)this.locations)) {
            final double locRandom = Rnd.get(100);
            float cumulativeChance = 0.0f;
            for (final ChanceLocation loc : this.locations) {
                if (locRandom <= (cumulativeChance += (float)loc.getChance())) {
                    return loc;
                }
            }
            NpcSpawnTemplate.LOGGER.warn("Couldn't match location by chance returning first..");
            return this.locations.get(0);
        }
        if (Objects.nonNull(this.zone)) {
            final Location loc2 = this.zone.getRandomPoint();
            loc2.setHeading(Rnd.get(65535));
            return loc2;
        }
        if (!this.group.getTerritories().isEmpty()) {
            return this.getRandomLocation(this.group.getTerritories(), this.group.getBannedTerritories());
        }
        if (!this.spawnTemplate.getTerritories().isEmpty()) {
            return this.getRandomLocation(this.spawnTemplate.getTerritories(), this.spawnTemplate.getBannedTerritories());
        }
        return null;
    }
    
    private Location getRandomLocation(final List<SpawnTerritory> territories, final List<BannedSpawnTerritory> bannedTerritories) {
        final SpawnTerritory territory = (SpawnTerritory)Rnd.get((List)territories);
        if (Objects.nonNull(territory)) {
            for (int i = 0; i < 25; ++i) {
                final Location loc = territory.getRandomPoint();
                final Location location;
                if (bannedTerritories.isEmpty() || bannedTerritories.stream().noneMatch(bannedTerritory -> bannedTerritory.isInsideZone(location.getX(), location.getY(), location.getZ()))) {
                    return loc;
                }
            }
        }
        return null;
    }
    
    public void spawn() {
        this.spawn(null);
    }
    
    public void spawn(final Instance instance) {
        try {
            final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(this.id);
            if (Objects.isNull(npcTemplate)) {
                NpcSpawnTemplate.LOGGER.warn("Attempting to spawn unexisting npc id: {} file: {} spawn {} group {}", new Object[] { this.id, this.spawnTemplate.getFilePath(), this.spawnTemplate.getName(), this.group.getName() });
                return;
            }
            if (npcTemplate.isType("Defender")) {
                NpcSpawnTemplate.LOGGER.warn("Attempting to spawn npc id: {} type: {} file: {} spawn: {} group: {}", new Object[] { this.id, npcTemplate.getType(), this.spawnTemplate.getFilePath(), this.spawnTemplate.getName(), this.group.getName() });
                return;
            }
            for (int i = 0; i < this._count; ++i) {
                this.spawnNpc(npcTemplate, instance);
            }
        }
        catch (Exception e) {
            NpcSpawnTemplate.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.id), (Throwable)e);
        }
    }
    
    private void spawnNpc(final NpcTemplate npcTemplate, final Instance instance) throws SecurityException, ClassNotFoundException, NoSuchMethodException, ClassCastException {
        final Spawn spawn = new Spawn(npcTemplate);
        final Location loc = this.getSpawnLocation();
        if (Objects.isNull(loc)) {
            NpcSpawnTemplate.LOGGER.warn("Couldn't initialize new spawn, no location found!");
            return;
        }
        spawn.setInstanceId(Util.zeroIfNullOrElse((Object)instance, Instance::getId));
        spawn.setAmount(1);
        spawn.setLocation(loc);
        final int respawn = Util.zeroIfNullOrElse((Object)this.respawnTime, r -> (int)r.getSeconds());
        final int respawnRandom = Util.zeroIfNullOrElse((Object)this.respawnTimeRandom, r -> (int)r.getSeconds());
        if (respawn > 0) {
            spawn.setRespawnDelay(respawn, respawnRandom);
            spawn.startRespawn();
        }
        else {
            spawn.stopRespawn();
        }
        spawn.setSpawnTemplate(this);
        if (this.saveInDB) {
            if (!DBSpawnManager.getInstance().isDefined(this.id)) {
                final Npc spawnedNpc = DBSpawnManager.getInstance().addNewSpawn(spawn, true);
                if (GameUtils.isMonster(spawnedNpc) && this._minions != null) {
                    ((Monster)spawnedNpc).getMinionList().spawnMinions(this._minions);
                }
                this._spawnedNpcs.add(spawnedNpc);
            }
        }
        else {
            final Npc npc = spawn.doSpawn(this._spawnAnimation);
            if (GameUtils.isMonster(npc) && this._minions != null) {
                ((Monster)npc).getMinionList().spawnMinions(this._minions);
            }
            this._spawnedNpcs.add(npc);
            SpawnTable.getInstance().addNewSpawn(spawn, false);
        }
    }
    
    public void despawn() {
        this._spawnedNpcs.forEach(npc -> {
            npc.getSpawn().stopRespawn();
            SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), false);
            npc.deleteMe();
            return;
        });
        this._spawnedNpcs.clear();
    }
    
    public void notifySpawnNpc(final Npc npc) {
        this.spawnTemplate.notifyEvent(event -> event.onSpawnNpc(this.spawnTemplate, this.group, npc));
    }
    
    public void notifyDespawnNpc(final Npc npc) {
        this.spawnTemplate.notifyEvent(event -> event.onSpawnDespawnNpc(this.spawnTemplate, this.group, npc));
    }
    
    public void notifyNpcDeath(final Npc npc, final Creature killer) {
        this.spawnTemplate.notifyEvent(event -> event.onSpawnNpcDeath(this.spawnTemplate, this.group, npc, killer));
    }
    
    public NpcSpawnTemplate clone() {
        return new NpcSpawnTemplate(this);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SpawnTemplate.class);
    }
}
