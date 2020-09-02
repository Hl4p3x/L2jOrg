// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.Iterator;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Map;

public class CreatureFollowTaskManager
{
    private static final Map<Creature, Integer> NORMAL_FOLLOW_CREATURES;
    private static final Map<Creature, Integer> ATTACK_FOLLOW_CREATURES;
    private static boolean _workingNormal;
    private static boolean _workingAttack;
    
    public CreatureFollowTaskManager() {
        final Iterator<Map.Entry<Creature, Integer>> iterator;
        Map.Entry<Creature, Integer> entry;
        ThreadPool.scheduleAtFixedRate(() -> {
            if (CreatureFollowTaskManager._workingNormal) {
                return;
            }
            else {
                CreatureFollowTaskManager._workingNormal = true;
                CreatureFollowTaskManager.NORMAL_FOLLOW_CREATURES.entrySet().iterator();
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    this.follow(entry.getKey(), entry.getValue());
                }
                CreatureFollowTaskManager._workingNormal = false;
                return;
            }
        }, 1000L, 1000L);
        final Iterator<Map.Entry<Creature, Integer>> iterator2;
        Map.Entry<Creature, Integer> entry2;
        ThreadPool.scheduleAtFixedRate(() -> {
            if (!CreatureFollowTaskManager._workingAttack) {
                CreatureFollowTaskManager._workingAttack = true;
                CreatureFollowTaskManager.ATTACK_FOLLOW_CREATURES.entrySet().iterator();
                while (iterator2.hasNext()) {
                    entry2 = iterator2.next();
                    this.follow(entry2.getKey(), entry2.getValue());
                }
                CreatureFollowTaskManager._workingAttack = false;
            }
        }, 500L, 500L);
    }
    
    private void follow(final Creature creature, final int range) {
        if (creature.hasAI()) {
            final CreatureAI ai = creature.getAI();
            if (ai != null) {
                final WorldObject followTarget = ai.getTarget();
                if (followTarget == null) {
                    if (GameUtils.isSummon(creature)) {
                        ((Summon)creature).setFollowStatus(false);
                    }
                    ai.setIntention(CtrlIntention.AI_INTENTION_IDLE);
                    return;
                }
                final int followRange = (range == -1) ? Rnd.get(50, 100) : range;
                if (!MathUtil.isInsideRadius3D(creature, followTarget, followRange)) {
                    if (!MathUtil.isInsideRadius3D(creature, followTarget, 3000)) {
                        if (GameUtils.isSummon(creature)) {
                            ((Summon)creature).setFollowStatus(false);
                        }
                        ai.setIntention(CtrlIntention.AI_INTENTION_IDLE);
                        return;
                    }
                    ai.moveToPawn(followTarget, followRange);
                }
            }
            else {
                this.remove(creature);
            }
        }
        else {
            this.remove(creature);
        }
    }
    
    public void addNormalFollow(final Creature creature, final int range) {
        CreatureFollowTaskManager.NORMAL_FOLLOW_CREATURES.putIfAbsent(creature, range);
    }
    
    public void addAttackFollow(final Creature creature, final int range) {
        CreatureFollowTaskManager.ATTACK_FOLLOW_CREATURES.putIfAbsent(creature, range);
    }
    
    public void remove(final Creature creature) {
        CreatureFollowTaskManager.NORMAL_FOLLOW_CREATURES.remove(creature);
        CreatureFollowTaskManager.ATTACK_FOLLOW_CREATURES.remove(creature);
    }
    
    public static CreatureFollowTaskManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    static {
        NORMAL_FOLLOW_CREATURES = new ConcurrentHashMap<Creature, Integer>();
        ATTACK_FOLLOW_CREATURES = new ConcurrentHashMap<Creature, Integer>();
        CreatureFollowTaskManager._workingNormal = false;
        CreatureFollowTaskManager._workingAttack = false;
    }
    
    private static class SingletonHolder
    {
        protected static final CreatureFollowTaskManager INSTANCE;
        
        static {
            INSTANCE = new CreatureFollowTaskManager();
        }
    }
}
