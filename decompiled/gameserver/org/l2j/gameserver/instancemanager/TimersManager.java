// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.events.timers.TimerHolder;
import java.util.List;
import java.util.Map;

public class TimersManager
{
    private final Map<Integer, List<TimerHolder<?>>> _timers;
    
    private TimersManager() {
        this._timers = new ConcurrentHashMap<Integer, List<TimerHolder<?>>>();
    }
    
    public void registerTimer(final TimerHolder<?> timer) {
        final Npc npc = timer.getNpc();
        if (npc != null) {
            final List<TimerHolder<?>> npcTimers = this._timers.computeIfAbsent(Integer.valueOf(npc.getObjectId()), key -> new ArrayList());
            synchronized (npcTimers) {
                npcTimers.add(timer);
            }
        }
        final Player player = timer.getPlayer();
        if (player != null) {
            final List<TimerHolder<?>> playerTimers = this._timers.computeIfAbsent(Integer.valueOf(player.getObjectId()), key -> new ArrayList());
            synchronized (playerTimers) {
                playerTimers.add(timer);
            }
        }
    }
    
    public void cancelTimers(final int objectId) {
        final List<TimerHolder<?>> timers = this._timers.remove(objectId);
        if (timers != null) {
            synchronized (timers) {
                timers.forEach(TimerHolder::cancelTimer);
            }
        }
    }
    
    public void unregisterTimer(final int objectId, final TimerHolder<?> timer) {
        final List<TimerHolder<?>> timers = this._timers.get(objectId);
        if (timers != null) {
            synchronized (timers) {
                timers.remove(timer);
            }
        }
    }
    
    public static TimersManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final TimersManager INSTANCE;
        
        static {
            INSTANCE = new TimersManager();
        }
    }
}
