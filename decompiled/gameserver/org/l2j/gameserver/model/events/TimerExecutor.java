// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.StatsSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.events.timers.IEventTimerCancel;
import org.l2j.gameserver.model.events.timers.IEventTimerEvent;
import org.l2j.gameserver.model.events.timers.TimerHolder;
import java.util.Set;
import java.util.Map;

public final class TimerExecutor<T>
{
    private final Map<T, Set<TimerHolder<T>>> _timers;
    private final IEventTimerEvent<T> _eventListener;
    private final IEventTimerCancel<T> _cancelListener;
    
    public TimerExecutor(final IEventTimerEvent<T> eventListener, final IEventTimerCancel<T> cancelListener) {
        this._timers = new ConcurrentHashMap<T, Set<TimerHolder<T>>>();
        this._eventListener = eventListener;
        this._cancelListener = cancelListener;
    }
    
    private boolean addTimer(final TimerHolder<T> holder) {
        final Set<TimerHolder<T>> timers2;
        final Set<TimerHolder<T>> timers = timers2 = this._timers.computeIfAbsent(holder.getEvent(), key -> ConcurrentHashMap.newKeySet());
        Objects.requireNonNull(holder);
        this.removeAndCancelTimers(timers2, holder::isEqual);
        return timers.add(holder);
    }
    
    public boolean addTimer(final T event, final StatsSet params, final long time, final Npc npc, final Player player, final IEventTimerEvent<T> eventTimer) {
        return this.addTimer(new TimerHolder<T>(event, params, time, npc, player, false, eventTimer, this._cancelListener, this));
    }
    
    public boolean addTimer(final T event, final long time, final IEventTimerEvent<T> eventTimer) {
        return this.addTimer(new TimerHolder<T>(event, null, time, null, null, false, eventTimer, this._cancelListener, this));
    }
    
    public boolean addTimer(final T event, final StatsSet params, final long time, final Npc npc, final Player player) {
        return this.addTimer(event, params, time, npc, player, this._eventListener);
    }
    
    public boolean addTimer(final T event, final long time, final Npc npc, final Player player) {
        return this.addTimer(event, null, time, npc, player, this._eventListener);
    }
    
    private boolean addRepeatingTimer(final T event, final StatsSet params, final long time, final Npc npc, final Player player, final IEventTimerEvent<T> eventTimer) {
        return this.addTimer(new TimerHolder<T>(event, params, time, npc, player, true, eventTimer, this._cancelListener, this));
    }
    
    public boolean addRepeatingTimer(final T event, final long time, final Npc npc, final Player player) {
        return this.addRepeatingTimer(event, null, time, npc, player, this._eventListener);
    }
    
    public void onTimerPostExecute(final TimerHolder<T> holder) {
        if (!holder.isRepeating()) {
            final Set<TimerHolder<T>> timers = this._timers.get(holder.getEvent());
            if (timers == null || timers.isEmpty()) {
                return;
            }
            final Set<TimerHolder<T>> timers2 = timers;
            Objects.requireNonNull(holder);
            this.removeAndCancelTimers(timers2, holder::isEqual);
            if (timers.isEmpty()) {
                this._timers.remove(holder.getEvent());
            }
        }
    }
    
    public void cancelAllTimers() {
        this._timers.values().stream().flatMap((Function<? super Set<TimerHolder<T>>, ? extends Stream<?>>)Collection::stream).forEach(TimerHolder::cancelTimer);
        this._timers.clear();
    }
    
    public boolean hasTimer(final T event, final Npc npc, final Player player) {
        final Set<TimerHolder<T>> timers = this._timers.get(event);
        return timers != null && !timers.isEmpty() && timers.stream().anyMatch(holder -> holder.isEqual(event, npc, player));
    }
    
    public boolean cancelTimers(final T event) {
        final Set<TimerHolder<T>> timers = this._timers.remove(event);
        if (timers == null || timers.isEmpty()) {
            return false;
        }
        timers.forEach(TimerHolder::cancelTimer);
        return true;
    }
    
    public boolean cancelTimer(final T event, final Npc npc, final Player player) {
        final Set<TimerHolder<T>> timers = this._timers.get(event);
        if (timers == null || timers.isEmpty()) {
            return false;
        }
        this.removeAndCancelTimers(timers, timer -> timer.isEqual(event, npc, player));
        return false;
    }
    
    public void cancelTimersOf(final Npc npc) {
        this.removeAndCancelTimers(timer -> timer.getNpc() == npc);
    }
    
    private void removeAndCancelTimers(final Predicate<TimerHolder<T>> condition) {
        Objects.requireNonNull(condition);
        final Collection<Set<TimerHolder<T>>> allTimers = this._timers.values();
        for (final Set<TimerHolder<T>> timers : allTimers) {
            this.removeAndCancelTimers(timers, condition);
        }
    }
    
    private void removeAndCancelTimers(final Set<TimerHolder<T>> timers, final Predicate<TimerHolder<T>> condition) {
        Objects.requireNonNull(timers);
        Objects.requireNonNull(condition);
        final Iterator<TimerHolder<T>> it = timers.iterator();
        while (it.hasNext()) {
            final TimerHolder<T> timer = it.next();
            if (condition.test(timer)) {
                it.remove();
                timer.cancelTimer();
            }
        }
    }
}
