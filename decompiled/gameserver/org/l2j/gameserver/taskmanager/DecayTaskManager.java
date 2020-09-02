// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Map;
import org.slf4j.Logger;

public final class DecayTaskManager
{
    protected static final Logger LOGGER;
    private static final Map<Creature, Long> DECAY_SCHEDULES;
    
    private DecayTaskManager() {
        final long time;
        final Iterator<Map.Entry<Creature, Long>> iterator;
        Map.Entry<Creature, Long> entry;
        Creature creature;
        ThreadPool.scheduleAtFixedRate(() -> {
            time = System.currentTimeMillis();
            DecayTaskManager.DECAY_SCHEDULES.entrySet().iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (time > entry.getValue()) {
                    creature = entry.getKey();
                    DecayTaskManager.DECAY_SCHEDULES.remove(creature);
                    creature.onDecay();
                }
            }
        }, 0L, 1000L);
    }
    
    public void add(final Creature character) {
        if (character == null) {
            return;
        }
        long delay;
        if (character.getTemplate() instanceof NpcTemplate) {
            delay = ((NpcTemplate)character.getTemplate()).getCorpseTime();
        }
        else {
            delay = Config.DEFAULT_CORPSE_TIME;
        }
        if (GameUtils.isAttackable(character) && (((Attackable)character).isSpoiled() || ((Attackable)character).isSeeded())) {
            delay += Config.SPOILED_CORPSE_EXTEND_TIME;
        }
        DecayTaskManager.DECAY_SCHEDULES.put(character, System.currentTimeMillis() + delay * 1000L);
    }
    
    public void cancel(final Creature creature) {
        DecayTaskManager.DECAY_SCHEDULES.remove(creature);
    }
    
    public long getRemainingTime(final Creature creature) {
        final Long time = DecayTaskManager.DECAY_SCHEDULES.get(creature);
        return (time != null) ? (time - System.currentTimeMillis()) : Long.MAX_VALUE;
    }
    
    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append("============= DecayTask Manager Report ============");
        ret.append(System.lineSeparator());
        ret.append("Tasks count: ");
        ret.append(DecayTaskManager.DECAY_SCHEDULES.size());
        ret.append(System.lineSeparator());
        ret.append("Tasks dump:");
        ret.append(System.lineSeparator());
        final long time = System.currentTimeMillis();
        for (final Map.Entry<Creature, Long> entry : DecayTaskManager.DECAY_SCHEDULES.entrySet()) {
            ret.append("Class/Name: ");
            ret.append(entry.getKey().getClass().getSimpleName());
            ret.append('/');
            ret.append(entry.getKey().getName());
            ret.append(" decay timer: ");
            ret.append(entry.getValue() - time);
            ret.append(System.lineSeparator());
        }
        return ret.toString();
    }
    
    public static DecayTaskManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)DecayTaskManager.class);
        DECAY_SCHEDULES = new ConcurrentHashMap<Creature, Long>();
    }
    
    private static class Singleton
    {
        private static final DecayTaskManager INSTANCE;
        
        static {
            INSTANCE = new DecayTaskManager();
        }
    }
}
