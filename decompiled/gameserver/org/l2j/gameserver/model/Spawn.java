// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.taskmanager.RespawnTaskManager;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import java.lang.reflect.Constructor;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Deque;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public class Spawn extends Location implements IIdentifiable, INamable
{
    protected static final Logger LOGGER;
    private final Deque<Npc> _spawnedNpcs;
    public int _scheduledCount;
    private String _name;
    private NpcTemplate _template;
    private int _maximumCount;
    private int _currentCount;
    private int _locationId;
    private int _instanceId;
    private int _respawnMinDelay;
    private int _respawnMaxDelay;
    private Constructor<? extends Npc> _constructor;
    private boolean _doRespawn;
    private boolean _randomWalk;
    private NpcSpawnTemplate _spawnTemplate;
    
    public Spawn(final NpcTemplate template) throws SecurityException, ClassNotFoundException, NoSuchMethodException, ClassCastException {
        super(0, 0, -10000);
        this._spawnedNpcs = new ConcurrentLinkedDeque<Npc>();
        this._instanceId = 0;
        this._doRespawn = true;
        this._randomWalk = false;
        this._template = template;
        if (this._template == null) {
            return;
        }
        final String className = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._template.getType());
        this._constructor = Class.forName(className).asSubclass(Npc.class).getConstructor(NpcTemplate.class);
    }
    
    public Spawn(final int npcId) throws SecurityException, ClassNotFoundException, NoSuchMethodException, ClassCastException {
        super(0, 0, -10000);
        this._spawnedNpcs = new ConcurrentLinkedDeque<Npc>();
        this._instanceId = 0;
        this._doRespawn = true;
        this._randomWalk = false;
        this._template = Objects.requireNonNull(NpcData.getInstance().getTemplate(npcId), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId));
        final String className = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._template.getType());
        this._constructor = Class.forName(className).asSubclass(Npc.class).getConstructor(NpcTemplate.class);
    }
    
    public int getAmount() {
        return this._maximumCount;
    }
    
    public void setAmount(final int amount) {
        this._maximumCount = amount;
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public int getLocationId() {
        return this._locationId;
    }
    
    public void setLocationId(final int id) {
        this._locationId = id;
    }
    
    @Override
    public int getId() {
        return this._template.getId();
    }
    
    public int getRespawnMinDelay() {
        return this._respawnMinDelay;
    }
    
    public void setRespawnMinDelay(final int date) {
        this._respawnMinDelay = date;
    }
    
    public int getRespawnMaxDelay() {
        return this._respawnMaxDelay;
    }
    
    public void setRespawnMaxDelay(final int date) {
        this._respawnMaxDelay = date;
    }
    
    public void decreaseCount(final Npc oldNpc) {
        if (this._currentCount <= 0) {
            return;
        }
        --this._currentCount;
        this._spawnedNpcs.remove(oldNpc);
        if (this._doRespawn && this._scheduledCount + this._currentCount < this._maximumCount) {
            ++this._scheduledCount;
            RespawnTaskManager.getInstance().add(oldNpc, System.currentTimeMillis() + (this.hasRespawnRandom() ? Rnd.get(this._respawnMinDelay, this._respawnMaxDelay) : this._respawnMinDelay));
        }
    }
    
    public int init() {
        while (this._currentCount < this._maximumCount) {
            this.doSpawn();
        }
        this._doRespawn = (this._respawnMinDelay > 0);
        return this._currentCount;
    }
    
    public boolean isRespawnEnabled() {
        return this._doRespawn;
    }
    
    public void stopRespawn() {
        this._doRespawn = false;
    }
    
    public void startRespawn() {
        this._doRespawn = true;
    }
    
    public Npc doSpawn() {
        return this._doRespawn ? this.doSpawn(false) : null;
    }
    
    public Npc doSpawn(final boolean isSummonSpawn) {
        try {
            if (this._template.isType("Pet") || this._template.isType("Decoy") || this._template.isType("Trap")) {
                ++this._currentCount;
                return null;
            }
            final Npc npc = (Npc)this._constructor.newInstance(this._template);
            npc.setInstanceById(this._instanceId);
            if (isSummonSpawn) {
                npc.setShowSummonAnimation(isSummonSpawn);
            }
            return this.initializeNpcInstance(npc);
        }
        catch (Exception e) {
            Spawn.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._template.getId()), (Throwable)e);
            return null;
        }
    }
    
    public void respawnNpc(final Npc oldNpc) {
        if (this._doRespawn) {
            oldNpc.refreshID();
            this.initializeNpcInstance(oldNpc);
            final Instance instance = oldNpc.getInstanceWorld();
            if (instance != null) {
                instance.addNpc(oldNpc);
            }
        }
    }
    
    private Npc initializeNpcInstance(final Npc npc) {
        int newLocZ = -10000;
        int newLocX;
        int newLocY;
        if (this._spawnTemplate != null) {
            final Location loc = this._spawnTemplate.getSpawnLocation();
            newLocX = loc.getX();
            newLocY = loc.getY();
            newLocZ = loc.getZ();
            this.setLocation(loc);
        }
        else {
            if (this.getX() == 0 && this.getY() == 0) {
                Spawn.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/Npc;)Ljava/lang/String;, npc));
                return null;
            }
            newLocX = this.getX();
            newLocY = this.getY();
            newLocZ = this.getZ();
        }
        if (Config.ENABLE_RANDOM_MONSTER_SPAWNS) {
            final int randX = newLocX + Rnd.get(Config.MOB_MIN_SPAWN_RANGE, Config.MOB_MAX_SPAWN_RANGE);
            final int randY = newLocY + Rnd.get(Config.MOB_MIN_SPAWN_RANGE, Config.MOB_MAX_SPAWN_RANGE);
            if (GameUtils.isMonster(npc) && !npc.isQuestMonster() && !GameUtils.isWalker(npc) && !npc.isInsideZone(ZoneType.NO_BOOKMARK) && this.getInstanceId() == 0 && GeoEngine.getInstance().canMoveToTarget(newLocX, newLocY, newLocZ, randX, randY, newLocZ, npc.getInstanceWorld()) && !this.getTemplate().isUndying() && !npc.isRaid() && !npc.isRaidMinion() && !Config.MOBS_LIST_NOT_RANDOM.contains(npc.getId())) {
                newLocX = randX;
                newLocY = randY;
            }
        }
        if (!npc.isFlying()) {
            final int geoZ = GeoEngine.getInstance().getHeight(newLocX, newLocY, newLocZ);
            if (MathUtil.isInsideRadius3D(newLocX, newLocY, newLocZ, newLocX, newLocY, geoZ, 300)) {
                newLocZ = geoZ;
            }
        }
        npc.setRandomWalking(this._randomWalk);
        if (this.getHeading() == -1) {
            npc.setHeading(Rnd.get(61794));
        }
        else {
            npc.setHeading(this.getHeading());
        }
        if (npc.getTemplate().isUsingServerSideName()) {
            npc.setName(npc.getTemplate().getName());
        }
        if (npc.getTemplate().isUsingServerSideTitle()) {
            npc.setTitle(npc.getTemplate().getTitle());
        }
        npc.onRespawn();
        npc.setSpawn(this);
        npc.spawnMe(newLocX, newLocY, newLocZ);
        if (this._spawnTemplate != null) {
            this._spawnTemplate.notifySpawnNpc(npc);
        }
        this._spawnedNpcs.add(npc);
        ++this._currentCount;
        if (GameUtils.isMonster(npc) && NpcData.getInstance().isMaster(npc.getId())) {
            ((Monster)npc).getMinionList().spawnMinions(npc.getParameters().getMinionList("Privates"));
        }
        return npc;
    }
    
    public void setRespawnDelay(final int delay, final int randomInterval) {
        if (delay != 0) {
            if (delay < 0) {
                Spawn.LOGGER.warn("respawn delay is negative for spawn: {}", (Object)this);
            }
            final int minDelay = delay - randomInterval;
            final int maxDelay = delay + randomInterval;
            this._respawnMinDelay = Math.max(10, minDelay) * 1000;
            this._respawnMaxDelay = Math.max(10, maxDelay) * 1000;
        }
        else {
            this._respawnMinDelay = 0;
            this._respawnMaxDelay = 0;
        }
    }
    
    public int getRespawnDelay() {
        return (this._respawnMinDelay + this._respawnMaxDelay) / 2;
    }
    
    public void setRespawnDelay(final int delay) {
        this.setRespawnDelay(delay, 0);
    }
    
    public boolean hasRespawnRandom() {
        return this._respawnMinDelay != this._respawnMaxDelay;
    }
    
    public Npc getLastSpawn() {
        if (!this._spawnedNpcs.isEmpty()) {
            return this._spawnedNpcs.peekLast();
        }
        return null;
    }
    
    public boolean deleteLastNpc() {
        return !this._spawnedNpcs.isEmpty() && this._spawnedNpcs.getLast().deleteMe();
    }
    
    public final Deque<Npc> getSpawnedNpcs() {
        return this._spawnedNpcs;
    }
    
    public NpcTemplate getTemplate() {
        return this._template;
    }
    
    public int getInstanceId() {
        return this._instanceId;
    }
    
    public void setInstanceId(final int instanceId) {
        this._instanceId = instanceId;
    }
    
    public final boolean getRandomWalking() {
        return this._randomWalk;
    }
    
    public final void setRandomWalking(final boolean value) {
        this._randomWalk = value;
    }
    
    public void setSpawnTemplate(final NpcSpawnTemplate npcSpawnTemplate) {
        this._spawnTemplate = npcSpawnTemplate;
    }
    
    public NpcSpawnTemplate getNpcSpawnTemplate() {
        return this._spawnTemplate;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(IIIII)Ljava/lang/String;, this._template.getId(), this.getX(), this.getY(), this.getZ(), this.getHeading());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Spawn.class);
    }
}
