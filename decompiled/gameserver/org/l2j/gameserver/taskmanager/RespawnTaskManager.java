// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.Spawn;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Map;

public class RespawnTaskManager
{
    private static final Map<Npc, Long> PENDING_RESPAWNS;
    
    public RespawnTaskManager() {
        final long time;
        final Iterator<Map.Entry<Npc, Long>> iterator;
        Map.Entry<Npc, Long> entry;
        Npc npc;
        Spawn spawn;
        final Spawn spawn2;
        ThreadPool.scheduleAtFixedRate(() -> {
            time = System.currentTimeMillis();
            RespawnTaskManager.PENDING_RESPAWNS.entrySet().iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (time > entry.getValue()) {
                    npc = entry.getKey();
                    RespawnTaskManager.PENDING_RESPAWNS.remove(npc);
                    spawn = npc.getSpawn();
                    if (spawn != null) {
                        spawn.respawnNpc(npc);
                        --spawn2._scheduledCount;
                    }
                    else {
                        continue;
                    }
                }
            }
        }, 0L, 1000L);
    }
    
    public void add(final Npc npc, final long time) {
        RespawnTaskManager.PENDING_RESPAWNS.put(npc, time);
    }
    
    public static RespawnTaskManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        PENDING_RESPAWNS = new ConcurrentHashMap<Npc, Long>();
    }
    
    private static class Singleton
    {
        private static final RespawnTaskManager INSTANCE;
        
        static {
            INSTANCE = new RespawnTaskManager();
        }
    }
}
