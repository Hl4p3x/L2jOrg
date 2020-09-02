// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.timers;

import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.instancemanager.TimersManager;
import org.l2j.commons.threading.ThreadPool;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.events.TimerExecutor;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.StatsSet;

public class TimerHolder<T> implements Runnable
{
    private final T _event;
    private final StatsSet _params;
    private final long _time;
    private final Npc _npc;
    private final Player _player;
    private final boolean _isRepeating;
    private final IEventTimerEvent<T> _eventScript;
    private final IEventTimerCancel<T> _cancelScript;
    private final TimerExecutor<T> _postExecutor;
    private final ScheduledFuture<?> _task;
    
    public TimerHolder(final T event, final StatsSet params, final long time, final Npc npc, final Player player, final boolean isRepeating, final IEventTimerEvent<T> eventScript, final IEventTimerCancel<T> cancelScript, final TimerExecutor<T> postExecutor) {
        Objects.requireNonNull(event, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        Objects.requireNonNull(eventScript, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        Objects.requireNonNull(postExecutor, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
        this._event = event;
        this._params = params;
        this._time = time;
        this._npc = npc;
        this._player = player;
        this._isRepeating = isRepeating;
        this._eventScript = eventScript;
        this._cancelScript = cancelScript;
        this._postExecutor = postExecutor;
        this._task = (ScheduledFuture<?>)(isRepeating ? ThreadPool.scheduleAtFixedRate((Runnable)this, this._time, this._time) : ThreadPool.schedule((Runnable)this, this._time));
        TimersManager.getInstance().registerTimer(this);
    }
    
    public T getEvent() {
        return this._event;
    }
    
    public StatsSet getParams() {
        return this._params;
    }
    
    public Npc getNpc() {
        return this._npc;
    }
    
    public Player getPlayer() {
        return this._player;
    }
    
    public boolean isRepeating() {
        return this._isRepeating;
    }
    
    public boolean cancelTimer() {
        if (this._npc != null) {
            TimersManager.getInstance().unregisterTimer(this._npc.getObjectId(), this);
        }
        if (this._player != null) {
            TimersManager.getInstance().unregisterTimer(this._player.getObjectId(), this);
        }
        if (this._task.isCancelled() || this._task.isDone()) {
            return false;
        }
        this._task.cancel(true);
        this._cancelScript.onTimerCancel(this);
        return true;
    }
    
    public long getRemainingTime() {
        if (this._task == null) {
            return -1L;
        }
        if (this._task.isCancelled() || this._task.isDone()) {
            return -1L;
        }
        return this._task.getDelay(TimeUnit.MILLISECONDS);
    }
    
    public boolean isEqual(final T event, final Npc npc, final Player player) {
        return this._event.equals(event) && this._npc == npc && this._player == player;
    }
    
    public boolean isEqual(final TimerHolder<T> timer) {
        return this._event.equals(timer._event) && this._npc == timer._npc && this._player == timer._player;
    }
    
    @Override
    public void run() {
        this._postExecutor.onTimerPostExecute(this);
        this._eventScript.onTimerEvent(this);
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Lorg/l2j/gameserver/model/StatsSet;JLorg/l2j/gameserver/model/actor/Npc;Lorg/l2j/gameserver/model/actor/instance/Player;ZLjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this._event, this._params, this._time, this._npc, this._player, this._isRepeating, this._eventScript.getClass().getSimpleName(), this._postExecutor.getClass().getSimpleName());
    }
}
