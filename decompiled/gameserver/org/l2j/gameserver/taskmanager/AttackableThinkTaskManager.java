// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.ai.CreatureAI;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Attackable;
import java.util.Set;

public class AttackableThinkTaskManager
{
    private static final Set<Attackable> ATTACKABLES;
    private static boolean _working;
    
    public AttackableThinkTaskManager() {
        final Iterator<Attackable> iterator;
        Attackable attackable;
        CreatureAI ai;
        ThreadPool.scheduleAtFixedRate(() -> {
            if (!AttackableThinkTaskManager._working) {
                AttackableThinkTaskManager._working = true;
                AttackableThinkTaskManager.ATTACKABLES.iterator();
                while (iterator.hasNext()) {
                    attackable = iterator.next();
                    if (attackable.hasAI()) {
                        ai = attackable.getAI();
                        if (ai != null) {
                            ai.onEvtThink();
                        }
                        else {
                            this.remove(attackable);
                        }
                    }
                    else {
                        this.remove(attackable);
                    }
                }
                AttackableThinkTaskManager._working = false;
            }
        }, 1000L, 1000L);
    }
    
    public void add(final Attackable attackable) {
        if (!AttackableThinkTaskManager.ATTACKABLES.contains(attackable)) {
            AttackableThinkTaskManager.ATTACKABLES.add(attackable);
        }
    }
    
    public void remove(final Attackable attackable) {
        AttackableThinkTaskManager.ATTACKABLES.remove(attackable);
    }
    
    public static AttackableThinkTaskManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    static {
        ATTACKABLES = ConcurrentHashMap.newKeySet();
        AttackableThinkTaskManager._working = false;
    }
    
    private static class SingletonHolder
    {
        protected static final AttackableThinkTaskManager INSTANCE;
        
        static {
            INSTANCE = new AttackableThinkTaskManager();
        }
    }
}
