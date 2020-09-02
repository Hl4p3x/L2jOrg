// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceLeave;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceEnter;
import org.l2j.gameserver.enums.InstanceTeleportType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.InstanceReenterType;
import org.l2j.gameserver.Config;
import java.sql.PreparedStatement;
import java.sql.Connection;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceDestroy;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.model.actor.Creature;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.Collections;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import java.util.Collection;
import java.util.Iterator;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceStatusChange;
import java.util.stream.Stream;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceCreated;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.instancemanager.InstanceManager;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Door;
import java.util.Map;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public final class Instance implements IIdentifiable, INamable
{
    private static final Logger LOGGER;
    private final int _id;
    private final InstanceTemplate _template;
    private final Set<Player> _allowed;
    private final Set<Player> _players;
    private final Set<Npc> _npcs;
    private final Map<Integer, Door> _doors;
    private final StatsSet _parameters;
    private final Map<Integer, ScheduledFuture<?>> _ejectDeadTasks;
    private final List<SpawnTemplate> _spawns;
    private long _endTime;
    private ScheduledFuture<?> _cleanUpTask;
    private ScheduledFuture<?> _emptyDestroyTask;
    
    public Instance(final int id, final InstanceTemplate template, final Player player) {
        this._allowed = (Set<Player>)ConcurrentHashMap.newKeySet();
        this._players = (Set<Player>)ConcurrentHashMap.newKeySet();
        this._npcs = (Set<Npc>)ConcurrentHashMap.newKeySet();
        this._doors = new HashMap<Integer, Door>();
        this._parameters = new StatsSet();
        this._ejectDeadTasks = new ConcurrentHashMap<Integer, ScheduledFuture<?>>();
        this._cleanUpTask = null;
        this._emptyDestroyTask = null;
        this._id = id;
        this._template = template;
        this._spawns = new ArrayList<SpawnTemplate>(template.getSpawns().size());
        final Stream<Object> map = template.getSpawns().stream().map((Function<? super Object, ?>)SpawnTemplate::clone);
        final List<SpawnTemplate> spawns = this._spawns;
        Objects.requireNonNull(spawns);
        map.forEach(spawns::add);
        InstanceManager.getInstance().register(this);
        this.setDuration(this._template.getDuration());
        this.setStatus(0);
        this.spawnDoors();
        this._spawns.stream().filter(SpawnTemplate::isSpawningByDefault).forEach(spawnTemplate -> spawnTemplate.spawnAll(this));
        if (!this.isDynamic()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnInstanceCreated(this, player), this._template);
        }
    }
    
    @Override
    public int getId() {
        return this._id;
    }
    
    @Override
    public String getName() {
        return this._template.getName();
    }
    
    public boolean isDynamic() {
        return this._template.getId() == -1;
    }
    
    public void setParameter(final String key, final Object val) {
        if (val == null) {
            this._parameters.remove(key);
        }
        else {
            this._parameters.set(key, val);
        }
    }
    
    public StatsSet getParameters() {
        return this._parameters;
    }
    
    public int getStatus() {
        return this._parameters.getInt("INSTANCE_STATUS", 0);
    }
    
    public void setStatus(final int value) {
        this._parameters.set("INSTANCE_STATUS", value);
        EventDispatcher.getInstance().notifyEventAsync(new OnInstanceStatusChange(this, value), this._template);
    }
    
    public int incStatus() {
        final int status = this.getStatus() + 1;
        this.setStatus(status);
        return status;
    }
    
    public void addAllowed(final Player player) {
        if (!this._allowed.contains(player)) {
            this._allowed.add(player);
        }
    }
    
    public boolean isAllowed(final Player player) {
        return this._allowed.contains(player);
    }
    
    public Set<Player> getAllowed() {
        return this._allowed;
    }
    
    public void addPlayer(final Player player) {
        this._players.add(player);
        if (this._emptyDestroyTask != null) {
            this._emptyDestroyTask.cancel(false);
            this._emptyDestroyTask = null;
        }
    }
    
    public void removePlayer(final Player player) {
        this._players.remove(player);
        if (this._players.isEmpty()) {
            final long emptyTime = this._template.getEmptyDestroyTime();
            if (this._template.getDuration() == 0 || emptyTime == 0L) {
                this.destroy();
            }
            else if (emptyTime >= 0L && this._emptyDestroyTask == null && this.getRemainingTime() < emptyTime) {
                this._emptyDestroyTask = (ScheduledFuture<?>)ThreadPool.schedule(this::destroy, emptyTime);
            }
        }
    }
    
    public boolean containsPlayer(final Player player) {
        return this._players.contains(player);
    }
    
    public Set<Player> getPlayers() {
        return this._players;
    }
    
    public int getPlayersCount() {
        return this._players.size();
    }
    
    private void spawnDoors() {
        for (final DoorTemplate template : this._template.getDoors().values()) {
            this._doors.put(template.getId(), DoorDataManager.getInstance().spawnDoor(template, this));
        }
    }
    
    public Collection<Door> getDoors() {
        return this._doors.values();
    }
    
    public Door getDoor(final int id) {
        return this._doors.get(id);
    }
    
    public List<SpawnGroup> getSpawnGroup(final String name) {
        final List<SpawnGroup> spawns = new ArrayList<SpawnGroup>();
        this._spawns.forEach(spawnTemplate -> spawns.addAll(spawnTemplate.getGroupsByName(name)));
        return spawns;
    }
    
    public List<Npc> spawnGroup(final String name) {
        final List<SpawnGroup> spawns = this.getSpawnGroup(name);
        if (spawns == null) {
            Instance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, name, this._template.getName(), this._id));
            return Collections.emptyList();
        }
        final List<Npc> npcs = new LinkedList<Npc>();
        try {
            for (final SpawnGroup holder : spawns) {
                holder.spawnAll(this);
                holder.getSpawns().forEach(spawn -> npcs.addAll(spawn.getSpawnedNpcs()));
            }
        }
        catch (Exception e) {
            Instance.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, name, this._template.getName(), this._id));
        }
        return npcs;
    }
    
    public Set<Npc> getNpcs() {
        return this._npcs;
    }
    
    public Set<Npc> getAliveNpcs() {
        return this._npcs.stream().filter(n -> n.getCurrentHp() > 0.0).collect((Collector<? super Object, ?, Set<Npc>>)Collectors.toSet());
    }
    
    public Npc getNpc(final int id) {
        return this._npcs.stream().filter(n -> n.getId() == id).findFirst().orElse(null);
    }
    
    public void addNpc(final Npc npc) {
        this._npcs.add(npc);
    }
    
    public void removeNpc(final Npc npc) {
        this._npcs.remove(npc);
    }
    
    private void removePlayers() {
        this._players.forEach(this::ejectPlayer);
        this._players.clear();
    }
    
    private void removeDoors() {
        this._doors.values().stream().filter(Objects::nonNull).forEach(Creature::decayMe);
        this._doors.clear();
    }
    
    public void removeNpcs() {
        this._spawns.forEach(SpawnTemplate::despawnAll);
        this._npcs.forEach(Npc::deleteMe);
        this._npcs.clear();
    }
    
    public void setDuration(final int minutes) {
        if (minutes < 0) {
            this._endTime = -1L;
            return;
        }
        final long millis = TimeUnit.MINUTES.toMillis(minutes);
        if (this._cleanUpTask != null) {
            this._cleanUpTask.cancel(true);
            this._cleanUpTask = null;
        }
        if (this._emptyDestroyTask != null && millis < this._emptyDestroyTask.getDelay(TimeUnit.MILLISECONDS)) {
            this._emptyDestroyTask.cancel(true);
            this._emptyDestroyTask = null;
        }
        this._endTime = System.currentTimeMillis() + millis;
        if (minutes < 1) {
            this.destroy();
        }
        else {
            this.sendWorldDestroyMessage(minutes);
            if (minutes <= 5) {
                this._cleanUpTask = (ScheduledFuture<?>)ThreadPool.schedule(this::cleanUp, millis - 60000L);
            }
            else {
                this._cleanUpTask = (ScheduledFuture<?>)ThreadPool.schedule(this::cleanUp, millis - 300000L);
            }
        }
    }
    
    public synchronized void destroy() {
        if (this._cleanUpTask != null) {
            this._cleanUpTask.cancel(false);
            this._cleanUpTask = null;
        }
        if (this._emptyDestroyTask != null) {
            this._emptyDestroyTask.cancel(false);
            this._emptyDestroyTask = null;
        }
        this._ejectDeadTasks.values().forEach(t -> t.cancel(true));
        this._ejectDeadTasks.clear();
        if (!this.isDynamic()) {
            EventDispatcher.getInstance().notifyEvent(new OnInstanceDestroy(this), this._template);
        }
        this.removePlayers();
        this.removeDoors();
        this.removeNpcs();
        InstanceManager.getInstance().unregister(this.getId());
    }
    
    public void ejectPlayer(final Player player) {
        if (player.getInstanceWorld().equals(this)) {
            final Location loc = this._template.getExitLocation(player);
            if (loc != null) {
                player.teleToLocation(loc, null);
            }
            else {
                player.teleToLocation(TeleportWhereType.TOWN, null);
            }
        }
    }
    
    public void broadcastPacket(final ServerPacket... packets) {
        for (final Player player : this._players) {
            for (final ServerPacket packet : packets) {
                player.sendPacket(packet);
            }
        }
    }
    
    public long getRemainingTime() {
        return (this._endTime == -1L) ? -1L : (this._endTime - System.currentTimeMillis());
    }
    
    public long getEndTime() {
        return this._endTime;
    }
    
    public void setReenterTime() {
        this.setReenterTime(this._template.calculateReenterTime());
    }
    
    public void setReenterTime(final long time) {
        if (this._template.getId() == -1 && time > 0L) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("INSERT IGNORE INTO character_instance_time (charId,instanceId,time) VALUES (?,?,?)");
                try {
                    for (final Player player2 : this._allowed) {
                        if (player2 != null) {
                            ps.setInt(1, player2.getObjectId());
                            ps.setInt(2, this._template.getId());
                            ps.setLong(3, time);
                            ps.addBatch();
                        }
                    }
                    ps.executeBatch();
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_POSSIBLE_ENTRY_TIME_BY_USING_THE_COMMAND_INSTANCEZONE);
                    if (InstanceManager.getInstance().getInstanceName(this.getTemplateId()) != null) {
                        msg.addInstanceName(this._template.getId());
                    }
                    else {
                        msg.addString(this._template.getName());
                    }
                    final ServerPacket serverPacket;
                    this._allowed.forEach(player -> {
                        if (player != null) {
                            InstanceManager.getInstance().setReenterPenalty(player.getObjectId(), this.getTemplateId(), time);
                            if (player.isOnline()) {
                                player.sendPacket(serverPacket);
                            }
                        }
                        return;
                    });
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Instance.LOGGER.warn("Could not insert character instance reenter data: ", (Throwable)e);
        }
    }
    
    public void finishInstance() {
        this.finishInstance(Config.INSTANCE_FINISH_TIME);
    }
    
    public void finishInstance(final int delay) {
        if (this._template.getReenterType() == InstanceReenterType.ON_FINISH) {
            this.setReenterTime();
        }
        this.setDuration(delay);
    }
    
    public void onDeath(final Player player) {
        if (!player.isOnCustomEvent() && this._template.getEjectTime() > 0) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.IF_YOU_ARE_NOT_RESURRECTED_WITHIN_S1_MINUTE_S_YOU_WILL_BE_EXPELLED_FROM_THE_INSTANT_ZONE);
            sm.addInt(this._template.getEjectTime());
            player.sendPacket(sm);
            this._ejectDeadTasks.put(player.getObjectId(), ThreadPool.schedule(() -> {
                if (player.isDead()) {
                    this.ejectPlayer(player.getActingPlayer());
                }
            }, (long)(this._template.getEjectTime() * 60 * 1000)));
        }
    }
    
    public void doRevive(final Player player) {
        final ScheduledFuture<?> task = this._ejectDeadTasks.remove(player.getObjectId());
        if (task != null) {
            task.cancel(true);
        }
    }
    
    public void onInstanceChange(final WorldObject object, final boolean enter) {
        if (GameUtils.isPlayer(object)) {
            final Player player = object.getActingPlayer();
            if (enter) {
                this.addPlayer(player);
                if (this._template.getExitLocationType() == InstanceTeleportType.ORIGIN) {
                    player.setInstanceOrigin(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, player.getX(), player.getY(), player.getZ()));
                }
                if (this._template.isRemoveBuffEnabled()) {
                    this._template.removePlayerBuff(player);
                }
                if (!this.isDynamic()) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnInstanceEnter(player, this), this._template);
                }
            }
            else {
                this.removePlayer(player);
                if (!this.isDynamic()) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnInstanceLeave(player, this), this._template);
                }
            }
        }
        else if (GameUtils.isNpc(object)) {
            final Npc npc = (Npc)object;
            if (enter) {
                this.addNpc(npc);
            }
            else {
                if (npc.getSpawn() != null) {
                    npc.getSpawn().stopRespawn();
                }
                this.removeNpc(npc);
            }
        }
    }
    
    public void onPlayerLogout(final Player player) {
        this.removePlayer(player);
        if (Config.RESTORE_PLAYER_INSTANCE) {
            player.setInstanceRestore(this._id);
        }
        else {
            final Location loc = this.getExitLocation(player);
            if (loc != null) {
                player.setLocationInvisible(loc);
                final Summon pet = player.getPet();
                if (pet != null) {
                    pet.teleToLocation(loc, true);
                }
            }
        }
    }
    
    public int getTemplateId() {
        return this._template.getId();
    }
    
    public InstanceReenterType getReenterType() {
        return this._template.getReenterType();
    }
    
    public boolean isPvP() {
        return this._template.isPvP();
    }
    
    public boolean isPlayerSummonAllowed() {
        return this._template.isPlayerSummonAllowed();
    }
    
    public Location getEnterLocation() {
        return this._template.getEnterLocation();
    }
    
    public List<Location> getEnterLocations() {
        return this._template.getEnterLocations();
    }
    
    public Location getExitLocation(final Player player) {
        return this._template.getExitLocation(player);
    }
    
    public float getExpRate() {
        return this._template.getExpRate();
    }
    
    public float getSPRate() {
        return this._template.getSPRate();
    }
    
    public float getExpPartyRate() {
        return this._template.getExpPartyRate();
    }
    
    public float getSPPartyRate() {
        return this._template.getSPPartyRate();
    }
    
    private void cleanUp() {
        if (this.getRemainingTime() <= TimeUnit.MINUTES.toMillis(1L)) {
            this.sendWorldDestroyMessage(1);
            this._cleanUpTask = (ScheduledFuture<?>)ThreadPool.schedule(this::destroy, 60000L);
        }
        else {
            this.sendWorldDestroyMessage(5);
            this._cleanUpTask = (ScheduledFuture<?>)ThreadPool.schedule(this::cleanUp, 300000L);
        }
    }
    
    private void sendWorldDestroyMessage(final int delay) {
        if (delay > 5) {
            return;
        }
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THIS_INSTANT_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTE_S_YOU_WILL_BE_FORCED_OUT_OF_THE_DUNGEON_WHEN_THE_TIME_EXPIRES);
        sm.addInt(delay);
        this.broadcastPacket(sm);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Instance && ((Instance)obj).getId() == this.getId();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this._template.getName(), this._id);
    }
    
    public void openCloseDoor(final int id, final boolean open) {
        final Door door = this._doors.get(id);
        if (door != null) {
            if (open) {
                if (!door.isOpen()) {
                    door.openMe();
                }
            }
            else if (door.isOpen()) {
                door.closeMe();
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Instance.class);
    }
}
