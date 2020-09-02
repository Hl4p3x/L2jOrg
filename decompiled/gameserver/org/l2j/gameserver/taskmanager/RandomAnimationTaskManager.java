// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Map;

public class RandomAnimationTaskManager
{
    private static final Map<Npc, Long> PENDING_ANIMATIONS;
    
    public RandomAnimationTaskManager() {
        final long time;
        final Iterator<Map.Entry<Npc, Long>> iterator;
        Map.Entry<Npc, Long> entry;
        Npc npc;
        ThreadPool.scheduleAtFixedRate(() -> {
            time = System.currentTimeMillis();
            RandomAnimationTaskManager.PENDING_ANIMATIONS.entrySet().iterator();
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (time > entry.getValue()) {
                    npc = entry.getKey();
                    if (npc.isInActiveRegion() && !npc.isDead() && !npc.isInCombat() && !npc.isMoving() && !npc.hasBlockActions()) {
                        npc.onRandomAnimation(Rnd.get(2, 3));
                    }
                    RandomAnimationTaskManager.PENDING_ANIMATIONS.put(npc, time + Rnd.get(GameUtils.isAttackable(npc) ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION, GameUtils.isAttackable(npc) ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION) * 1000);
                }
            }
        }, 0L, 1000L);
    }
    
    public void add(final Npc npc) {
        if (npc.hasRandomAnimation()) {
            RandomAnimationTaskManager.PENDING_ANIMATIONS.putIfAbsent(npc, System.currentTimeMillis() + Rnd.get(GameUtils.isAttackable(npc) ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION, GameUtils.isAttackable(npc) ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION) * 1000);
        }
    }
    
    public void remove(final Npc npc) {
        RandomAnimationTaskManager.PENDING_ANIMATIONS.remove(npc);
    }
    
    public static RandomAnimationTaskManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    static {
        PENDING_ANIMATIONS = new ConcurrentHashMap<Npc, Long>();
    }
    
    private static class SingletonHolder
    {
        protected static final RandomAnimationTaskManager INSTANCE;
        
        static {
            INSTANCE = new RandomAnimationTaskManager();
        }
    }
}
