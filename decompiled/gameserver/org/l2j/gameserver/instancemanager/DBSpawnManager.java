// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import java.time.Duration;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.SpawnDAO;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.util.GameUtils;
import java.util.Date;
import org.l2j.commons.util.Rnd;
import java.util.List;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import java.util.Objects;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import io.github.joealisson.primitive.CHashIntMap;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public class DBSpawnManager
{
    private static final Logger LOGGER;
    private final IntMap<Npc> npcs;
    private final IntMap<Spawn> spawns;
    private final IntMap<StatsSet> storedInfo;
    private final IntMap<ScheduledFuture<?>> schedules;
    
    private DBSpawnManager() {
        this.npcs = (IntMap<Npc>)new CHashIntMap();
        this.spawns = (IntMap<Spawn>)new CHashIntMap();
        this.storedInfo = (IntMap<StatsSet>)new CHashIntMap();
        this.schedules = (IntMap<ScheduledFuture<?>>)new CHashIntMap();
    }
    
    private void load() {
        if (Config.ALT_DEV_NO_SPAWNS) {
            return;
        }
        this.npcs.clear();
        this.spawns.clear();
        this.storedInfo.clear();
        this.schedules.clear();
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT * FROM npc_respawns");
                try {
                    final ResultSet rset = statement.executeQuery();
                    try {
                        while (rset.next()) {
                            final NpcTemplate template = this.getValidTemplate(rset.getInt("id"));
                            if (Objects.nonNull(template)) {
                                final Spawn spawn = new Spawn(template);
                                spawn.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));
                                spawn.setAmount(1);
                                spawn.setHeading(rset.getInt("heading"));
                                final List<NpcSpawnTemplate> spawns = SpawnsData.getInstance().getNpcSpawns(npc -> npc.getId() == template.getId() && npc.hasDBSave());
                                if (spawns.isEmpty()) {
                                    DBSpawnManager.LOGGER.warn("Couldn't find spawn declaration for npc: {} - {} ", (Object)template.getId(), (Object)template.getName());
                                    this.deleteSpawn(spawn, true);
                                }
                                else if (spawns.size() > 1) {
                                    DBSpawnManager.LOGGER.warn("Found multiple database spawns for npc: {} - {} {}", new Object[] { template.getId(), template.getName(), spawns });
                                }
                                else {
                                    final NpcSpawnTemplate spawnTemplate = spawns.get(0);
                                    spawn.setSpawnTemplate(spawnTemplate);
                                    final int respawn = Util.zeroIfNullOrElse((Object)spawnTemplate.getRespawnTime(), d -> (int)d.getSeconds());
                                    final int respawnRandom = Util.zeroIfNullOrElse((Object)spawnTemplate.getRespawnTimeRandom(), d -> (int)d.getSeconds());
                                    if (respawn > 0) {
                                        spawn.setRespawnDelay(respawn, respawnRandom);
                                        spawn.startRespawn();
                                        this.addNewSpawn(spawn, rset.getLong("respawnTime"), rset.getDouble("currentHp"), rset.getDouble("currentMp"), false);
                                    }
                                    else {
                                        spawn.stopRespawn();
                                        DBSpawnManager.LOGGER.warn("Found database spawns without respawn for npc: {} - {} {}", new Object[] { template.getId(), template.getName(), spawnTemplate });
                                    }
                                }
                            }
                            else {
                                DBSpawnManager.LOGGER.warn("Could not load npc id {} from DB from DB", (Object)rset.getInt("id"));
                            }
                        }
                        DBSpawnManager.LOGGER.info("Loaded {} Instances", (Object)this.npcs.size());
                        DBSpawnManager.LOGGER.info("Scheduled {} Instances", (Object)this.schedules.size());
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (SQLException e) {
            DBSpawnManager.LOGGER.warn("Couldn't load npc_respawns table", (Throwable)e);
        }
        catch (Exception e2) {
            DBSpawnManager.LOGGER.warn("Error while initializing DBSpawnManager: ", (Throwable)e2);
        }
    }
    
    private void scheduleSpawn(final int npcId) {
        final Npc npc = ((Spawn)this.spawns.get(npcId)).doSpawn();
        if (npc != null) {
            npc.setRaidBossStatus(RaidBossStatus.ALIVE);
            final StatsSet info = new StatsSet();
            info.set("currentHP", npc.getCurrentHp());
            info.set("currentMP", npc.getCurrentMp());
            info.set("respawnTime", 0);
            this.storedInfo.put(npcId, (Object)info);
            this.npcs.put(npcId, (Object)npc);
            DBSpawnManager.LOGGER.info("Spawning NPC {}", (Object)npc.getName());
        }
        this.schedules.remove(npcId);
    }
    
    public void updateStatus(final Npc npc, final boolean isNpcDead) {
        final StatsSet info = (StatsSet)this.storedInfo.get(npc.getId());
        if (info == null) {
            return;
        }
        if (isNpcDead) {
            npc.setRaidBossStatus(RaidBossStatus.DEAD);
            final int respawnMinDelay = (int)(npc.getSpawn().getRespawnMinDelay() * Config.RAID_MIN_RESPAWN_MULTIPLIER);
            final int respawnMaxDelay = (int)(npc.getSpawn().getRespawnMaxDelay() * Config.RAID_MAX_RESPAWN_MULTIPLIER);
            final int respawnDelay = Rnd.get(respawnMinDelay, respawnMaxDelay);
            final long respawnTime = System.currentTimeMillis() + respawnDelay;
            info.set("currentHP", npc.getMaxHp());
            info.set("currentMP", npc.getMaxMp());
            info.set("respawnTime", respawnTime);
            if (!this.schedules.containsKey(npc.getId()) && (respawnMinDelay > 0 || respawnMaxDelay > 0)) {
                DBSpawnManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), npc.getName(), GameUtils.formatDate(new Date(respawnTime), "dd.MM.yyyy HH:mm")));
                this.schedules.put(npc.getId(), (Object)ThreadPool.schedule(() -> this.scheduleSpawn(npc.getId()), (long)respawnDelay));
                this.updateDb();
            }
        }
        else {
            npc.setRaidBossStatus(RaidBossStatus.ALIVE);
            info.set("currentHP", npc.getCurrentHp());
            info.set("currentMP", npc.getCurrentMp());
            info.set("respawnTime", 0);
        }
        this.storedInfo.put(npc.getId(), (Object)info);
    }
    
    public void addNewSpawn(final Spawn spawn, final long respawnTime, final double currentHP, final double currentMP, final boolean storeInDb) {
        if (Objects.isNull(spawn)) {
            return;
        }
        if (this.spawns.containsKey(spawn.getId())) {
            return;
        }
        final int npcId = spawn.getId();
        final long time = System.currentTimeMillis();
        SpawnTable.getInstance().addNewSpawn(spawn, false);
        if (respawnTime == 0L || time > respawnTime) {
            final Npc npc = spawn.doSpawn();
            if (npc != null) {
                npc.setCurrentHp(currentHP);
                npc.setCurrentMp(currentMP);
                npc.setRaidBossStatus(RaidBossStatus.ALIVE);
                this.npcs.put(npcId, (Object)npc);
                final StatsSet info = new StatsSet();
                info.set("currentHP", currentHP);
                info.set("currentMP", currentMP);
                info.set("respawnTime", 0);
                this.storedInfo.put(npcId, (Object)info);
            }
        }
        else {
            final long spawnTime = respawnTime - System.currentTimeMillis();
            this.schedules.put(npcId, (Object)ThreadPool.schedule(() -> this.scheduleSpawn(npcId), spawnTime));
        }
        this.spawns.put(npcId, (Object)spawn);
        if (storeInDb) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("INSERT INTO npc_respawns (id, x, y, z, heading, respawnTime, currentHp, currentMp) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
                    try {
                        statement.setInt(1, spawn.getId());
                        statement.setInt(2, spawn.getX());
                        statement.setInt(3, spawn.getY());
                        statement.setInt(4, spawn.getZ());
                        statement.setInt(5, spawn.getHeading());
                        statement.setLong(6, respawnTime);
                        statement.setDouble(7, currentHP);
                        statement.setDouble(8, currentMP);
                        statement.execute();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                DBSpawnManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), npcId), (Throwable)e);
            }
        }
    }
    
    public Npc addNewSpawn(final Spawn spawn, final boolean storeInDb) {
        if (spawn == null) {
            return null;
        }
        final int npcId = spawn.getId();
        final Spawn existingSpawn = (Spawn)this.spawns.get(npcId);
        if (existingSpawn != null) {
            return existingSpawn.getLastSpawn();
        }
        SpawnTable.getInstance().addNewSpawn(spawn, false);
        final Npc npc = spawn.doSpawn();
        if (npc == null) {
            throw new NullPointerException();
        }
        npc.setRaidBossStatus(RaidBossStatus.ALIVE);
        final StatsSet info = new StatsSet();
        info.set("currentHP", npc.getMaxHp());
        info.set("currentMP", npc.getMaxMp());
        info.set("respawnTime", 0);
        this.npcs.put(npcId, (Object)npc);
        this.storedInfo.put(npcId, (Object)info);
        this.spawns.put(npcId, (Object)spawn);
        if (storeInDb) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("INSERT INTO npc_respawns (id, x, y, z, heading, respawnTime, currentHp, currentMp) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
                    try {
                        statement.setInt(1, spawn.getId());
                        statement.setInt(2, spawn.getX());
                        statement.setInt(3, spawn.getY());
                        statement.setInt(4, spawn.getZ());
                        statement.setInt(5, spawn.getHeading());
                        statement.setLong(6, 0L);
                        statement.setDouble(7, npc.getMaxHp());
                        statement.setDouble(8, npc.getMaxMp());
                        statement.execute();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
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
                DBSpawnManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), npcId), (Throwable)e);
            }
        }
        return npc;
    }
    
    public void deleteSpawn(final Spawn spawn, final boolean updateDb) {
        if (spawn == null) {
            return;
        }
        final int npcId = spawn.getId();
        this.spawns.remove(npcId);
        this.npcs.remove(npcId);
        this.storedInfo.remove(npcId);
        final ScheduledFuture<?> task = (ScheduledFuture<?>)this.schedules.remove(npcId);
        if (task != null) {
            task.cancel(true);
        }
        if (updateDb) {
            ((SpawnDAO)DatabaseAccess.getDAO((Class)SpawnDAO.class)).deleteRespawn(npcId);
        }
        SpawnTable.getInstance().deleteSpawn(spawn, false);
    }
    
    private void updateDb() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("UPDATE npc_respawns SET respawnTime = ?, currentHP = ?, currentMP = ? WHERE id = ?");
                try {
                    final Npc npc;
                    StatsSet info;
                    final PreparedStatement preparedStatement;
                    this.storedInfo.keySet().forEach(npcId -> {
                        npc = (Npc)this.npcs.get(npcId);
                        if (npc == null) {
                            return;
                        }
                        else {
                            if (npc.getRaidBossStatus() == RaidBossStatus.ALIVE) {
                                this.updateStatus(npc, false);
                            }
                            info = (StatsSet)this.storedInfo.get(npcId);
                            if (info == null) {
                                return;
                            }
                            else {
                                try {
                                    preparedStatement.setLong(1, info.getLong("respawnTime"));
                                    preparedStatement.setDouble(2, npc.isDead() ? ((double)npc.getMaxHp()) : info.getDouble("currentHP"));
                                    preparedStatement.setDouble(3, npc.isDead() ? ((double)npc.getMaxMp()) : info.getDouble("currentMP"));
                                    preparedStatement.setInt(4, npcId);
                                    preparedStatement.executeUpdate();
                                    preparedStatement.clearParameters();
                                }
                                catch (SQLException e) {
                                    DBSpawnManager.LOGGER.warn("Couldnt update npc_respawns table ", (Throwable)e);
                                }
                                return;
                            }
                        }
                    });
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
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
        catch (SQLException e2) {
            DBSpawnManager.LOGGER.warn("SQL error while updating database spawn to database: ", (Throwable)e2);
        }
    }
    
    public RaidBossStatus getNpcStatusId(final int npcId) {
        if (this.npcs.containsKey(npcId)) {
            return ((Npc)this.npcs.get(npcId)).getRaidBossStatus();
        }
        if (this.schedules.containsKey(npcId)) {
            return RaidBossStatus.DEAD;
        }
        return RaidBossStatus.UNDEFINED;
    }
    
    public NpcTemplate getValidTemplate(final int npcId) {
        return NpcData.getInstance().getTemplate(npcId);
    }
    
    public void notifySpawnNightNpc(final Npc npc) {
        final StatsSet info = new StatsSet();
        info.set("currentHP", npc.getCurrentHp());
        info.set("currentMP", npc.getCurrentMp());
        info.set("respawnTime", 0);
        npc.setRaidBossStatus(RaidBossStatus.ALIVE);
        this.storedInfo.put(npc.getId(), (Object)info);
        this.npcs.put(npc.getId(), (Object)npc);
    }
    
    public boolean isDefined(final int npcId) {
        return this.spawns.containsKey(npcId);
    }
    
    public IntMap<Npc> getNpcs() {
        return this.npcs;
    }
    
    public IntMap<Spawn> getSpawns() {
        return this.spawns;
    }
    
    public void cleanUp() {
        this.updateDb();
        this.npcs.clear();
        this.schedules.values().forEach(s -> s.cancel(true));
        this.schedules.clear();
        this.storedInfo.clear();
        this.spawns.clear();
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static DBSpawnManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)DBSpawnManager.class);
    }
    
    private static class Singleton
    {
        private static final DBSpawnManager INSTANCE;
        
        static {
            INSTANCE = new DBSpawnManager();
        }
    }
}
