// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.AggroInfo;
import java.util.Map;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import java.util.Objects;
import java.util.Iterator;
import org.l2j.gameserver.model.holders.MinionHolder;
import java.util.List;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Monster;

public class MinionList
{
    protected final Monster _master;
    private final Collection<Monster> _spawnedMinions;
    private final Collection<ScheduledFuture<?>> _respawnTasks;
    
    public MinionList(final Monster pMaster) {
        this._spawnedMinions = (Collection<Monster>)ConcurrentHashMap.newKeySet();
        this._respawnTasks = (Collection<ScheduledFuture<?>>)ConcurrentHashMap.newKeySet();
        if (pMaster == null) {
            throw new NullPointerException("MinionList: master is null");
        }
        this._master = pMaster;
    }
    
    public static Monster spawnMinion(final Monster master, final int minionId) {
        final NpcTemplate minionTemplate = NpcData.getInstance().getTemplate(minionId);
        if (minionTemplate == null) {
            return null;
        }
        return initializeNpcInstance(master, new Monster(minionTemplate));
    }
    
    protected static Monster initializeNpcInstance(final Monster master, final Monster minion) {
        minion.stopAllEffects();
        minion.setIsDead(false);
        minion.setDecayed(false);
        minion.setCurrentHpMp(minion.getMaxHp(), minion.getMaxMp());
        minion.setHeading(master.getHeading());
        minion.setLeader(master);
        minion.setInstance(master.getInstanceWorld());
        final int offset = 200;
        final int minRadius = (int)master.getCollisionRadius() + 30;
        int newX = Rnd.get(minRadius * 2, 400);
        int newY = Rnd.get(newX, 400);
        newY = (int)Math.sqrt(newY * newY - newX * newX);
        if (newX > 200 + minRadius) {
            newX = master.getX() + newX - 200;
        }
        else {
            newX = master.getX() - newX + minRadius;
        }
        if (newY > 200 + minRadius) {
            newY = master.getY() + newY - 200;
        }
        else {
            newY = master.getY() - newY + minRadius;
        }
        minion.spawnMe(newX, newY, master.getZ());
        return minion;
    }
    
    public Collection<Monster> getSpawnedMinions() {
        return this._spawnedMinions;
    }
    
    public final void spawnMinions(final List<MinionHolder> minions) {
        if (this._master.isAlikeDead() || minions == null) {
            return;
        }
        for (final MinionHolder minion : minions) {
            final int minionCount = minion.getCount();
            final int minionId = minion.getId();
            final int minionsToSpawn = minionCount - this.countSpawnedMinionsById(minionId);
            if (minionsToSpawn > 0) {
                for (int i = 0; i < minionsToSpawn; ++i) {
                    this.spawnMinion(minionId);
                }
            }
        }
    }
    
    public void onMinionSpawn(final Monster minion) {
        this._spawnedMinions.add(minion);
    }
    
    public void onMasterDie(final boolean force) {
        this._spawnedMinions.forEach(minion -> {
            if (Objects.nonNull(minion)) {
                minion.setLeader(null);
                if (this._master.isRaid() || force || Config.FORCE_DELETE_MINIONS) {
                    minion.deleteMe();
                }
                else {
                    minion.scheduleDespawn(Config.DESPAWN_MINION_DELAY);
                }
            }
            return;
        });
        this._spawnedMinions.clear();
        this._respawnTasks.forEach(task -> {
            if (Objects.nonNull(task) && !task.isCancelled() && !task.isDone()) {
                task.cancel(true);
            }
            return;
        });
        this._respawnTasks.clear();
        this._master.setMinionList(null);
    }
    
    public void onMinionDie(final Monster minion, final int respawnTime) {
        minion.setLeader(null);
        this._spawnedMinions.remove(minion);
        final int time = (respawnTime < 0) ? (this._master.isRaid() ? ((int)Config.RAID_MINION_RESPAWN_TIMER) : 0) : respawnTime;
        if (time > 0 && !this._master.isAlikeDead()) {
            this._respawnTasks.add(ThreadPool.schedule((Runnable)new MinionRespawnTask(minion), (long)time));
        }
    }
    
    public void onAssist(final Creature caller, final Creature attacker) {
        if (attacker == null) {
            return;
        }
        if (!this._master.isAlikeDead() && !this._master.isInCombat()) {
            this._master.addDamageHate(attacker, 0, 1);
        }
        final boolean callerIsMaster = caller == this._master;
        int aggro = callerIsMaster ? 10 : 1;
        if (this._master.isRaid()) {
            aggro *= 10;
        }
        for (final Monster minion : this._spawnedMinions) {
            if (minion != null && !minion.isDead() && (callerIsMaster || !minion.isInCombat())) {
                minion.addDamageHate(attacker, 0, aggro);
            }
        }
    }
    
    public void onMasterTeleported() {
        final int offset = 200;
        final int minRadius = (int)this._master.getCollisionRadius() + 30;
        for (final Monster minion : this._spawnedMinions) {
            if (minion != null && !minion.isDead() && !minion.isMovementDisabled()) {
                int newX = Rnd.get(minRadius * 2, 400);
                int newY = Rnd.get(newX, 400);
                newY = (int)Math.sqrt(newY * newY - newX * newX);
                if (newX > 200 + minRadius) {
                    newX = this._master.getX() + newX - 200;
                }
                else {
                    newX = this._master.getX() - newX + minRadius;
                }
                if (newY > 200 + minRadius) {
                    newY = this._master.getY() + newY - 200;
                }
                else {
                    newY = this._master.getY() - newY + minRadius;
                }
                minion.teleToLocation(new Location(newX, newY, this._master.getZ()));
            }
        }
    }
    
    private final void spawnMinion(final int minionId) {
        if (minionId == 0) {
            return;
        }
        spawnMinion(this._master, minionId);
    }
    
    private final int countSpawnedMinionsById(final int minionId) {
        int count = 0;
        for (final Monster minion : this._spawnedMinions) {
            if (minion != null && minion.getId() == minionId) {
                ++count;
            }
        }
        return count;
    }
    
    public final int countSpawnedMinions() {
        return this._spawnedMinions.size();
    }
    
    public final long lazyCountSpawnedMinionsGroups() {
        return this._spawnedMinions.stream().distinct().count();
    }
    
    private final class MinionRespawnTask implements Runnable
    {
        private final Monster _minion;
        
        public MinionRespawnTask(final Monster minion) {
            this._minion = minion;
        }
        
        @Override
        public void run() {
            if (!MinionList.this._master.isAlikeDead() && MinionList.this._master.isSpawned()) {
                if (!this._minion.isSpawned()) {
                    this._minion.refreshID();
                    MinionList.initializeNpcInstance(MinionList.this._master, this._minion);
                }
                if (!MinionList.this._master.getAggroList().isEmpty()) {
                    this._minion.getAggroList().putAll(MinionList.this._master.getAggroList());
                    this._minion.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this._minion.getAggroList().keySet().stream().findFirst().get());
                }
            }
        }
    }
}
