// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.Collection;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.world.World;
import java.util.HashSet;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Set;

public class CreatureContainer
{
    private final Set<Integer> _seen;
    private final Creature _owner;
    private final int _range;
    private ScheduledFuture<?> _task;
    private Predicate<Creature> _condition;
    
    public CreatureContainer(final Creature owner, final int range, final Predicate<Creature> condition) {
        this._seen = (Set<Integer>)ConcurrentHashMap.newKeySet();
        this._condition = null;
        this._owner = owner;
        this._range = range;
        this._condition = condition;
        this.start();
    }
    
    public Creature getOwner() {
        return this._owner;
    }
    
    public int getRange() {
        return this._range;
    }
    
    public void start() {
        if (this._task == null || this._task.isDone()) {
            this._task = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate(this::update, 1000L, 1000L);
        }
    }
    
    public boolean stop() {
        return this._task != null && !this._task.isDone() && this._task.cancel(false);
    }
    
    public void reset() {
        this._seen.clear();
    }
    
    private void update() {
        final Set<Integer> verified = new HashSet<Integer>();
        final Set<Integer> set;
        World.getInstance().forEachVisibleObjectInRange(this._owner, Creature.class, this._range, creature -> {
            if (this._condition == null || this._condition.test(creature)) {
                if (this._seen.add(creature.getObjectId())) {
                    EventDispatcher.getInstance().notifyEventAsync(new OnCreatureSee(this._owner, creature), this._owner);
                }
                set.add(creature.getObjectId());
            }
            return;
        });
        this._seen.retainAll(verified);
    }
}
