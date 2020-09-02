// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import org.l2j.gameserver.model.actor.Summon;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.AutoAttackStop;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Map;
import org.slf4j.Logger;

public class AttackStanceTaskManager
{
    public static final long COMBAT_TIME = 15000L;
    protected static final Logger LOGGER;
    protected static final Map<Creature, Long> _attackStanceTasks;
    
    private AttackStanceTaskManager() {
        ThreadPool.scheduleAtFixedRate((Runnable)new FightModeScheduler(), 0L, 1000L);
    }
    
    public void addAttackStanceTask(final Creature actor) {
        if (actor != null) {
            AttackStanceTaskManager._attackStanceTasks.put(actor, System.currentTimeMillis());
        }
    }
    
    public void removeAttackStanceTask(Creature actor) {
        if (actor != null) {
            if (GameUtils.isSummon(actor)) {
                actor = actor.getActingPlayer();
            }
            AttackStanceTaskManager._attackStanceTasks.remove(actor);
        }
    }
    
    public boolean hasAttackStanceTask(Creature actor) {
        if (actor != null) {
            if (GameUtils.isSummon(actor)) {
                actor = actor.getActingPlayer();
            }
            return AttackStanceTaskManager._attackStanceTasks.containsKey(actor);
        }
        return false;
    }
    
    public static AttackStanceTaskManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AttackStanceTaskManager.class);
        _attackStanceTasks = new ConcurrentHashMap<Creature, Long>();
    }
    
    private static class Singleton
    {
        private static final AttackStanceTaskManager INSTANCE;
        
        static {
            INSTANCE = new AttackStanceTaskManager();
        }
    }
    
    protected class FightModeScheduler implements Runnable
    {
        @Override
        public void run() {
            final long current = System.currentTimeMillis();
            try {
                final Iterator<Map.Entry<Creature, Long>> iter = AttackStanceTaskManager._attackStanceTasks.entrySet().iterator();
                while (iter.hasNext()) {
                    final Map.Entry<Creature, Long> e = iter.next();
                    if (current - e.getValue() > 15000L) {
                        final Creature actor = e.getKey();
                        if (actor != null) {
                            actor.broadcastPacket(new AutoAttackStop(actor.getObjectId()));
                            actor.getAI().setAutoAttacking(false);
                            if (GameUtils.isPlayer(actor) && actor.hasSummon()) {
                                final Summon pet = actor.getPet();
                                if (pet != null) {
                                    pet.broadcastPacket(new AutoAttackStop(pet.getObjectId()));
                                }
                                actor.getServitors().values().forEach(s -> s.broadcastPacket(new AutoAttackStop(s.getObjectId())));
                            }
                        }
                        iter.remove();
                    }
                }
            }
            catch (Exception e2) {
                AttackStanceTaskManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e2.getMessage()), (Throwable)e2);
            }
        }
    }
}
