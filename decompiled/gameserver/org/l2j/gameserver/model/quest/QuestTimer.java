// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.quest;

import org.slf4j.LoggerFactory;
import org.l2j.commons.threading.ThreadPool;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.slf4j.Logger;

public class QuestTimer
{
    protected static final Logger LOGGER;
    final String _name;
    final Quest _quest;
    final Npc _npc;
    final Player _player;
    final boolean _isRepeating;
    private final ScheduledFuture<?> _scheduler;
    boolean _isActive;
    
    public QuestTimer(final Quest quest, final String name, final long time, final Npc npc, final Player player, final boolean repeating) {
        this._isActive = true;
        this._name = name;
        this._quest = quest;
        this._player = player;
        this._npc = npc;
        this._isRepeating = repeating;
        this._scheduler = (ScheduledFuture<?>)(repeating ? ThreadPool.scheduleAtFixedRate((Runnable)new ScheduleTimerTask(), time, time) : ThreadPool.schedule((Runnable)new ScheduleTimerTask(), time));
    }
    
    public QuestTimer(final Quest quest, final String name, final long time, final Npc npc, final Player player) {
        this(quest, name, time, npc, player, false);
    }
    
    public QuestTimer(final QuestState qs, final String name, final long time) {
        this(qs.getQuest(), name, time, null, qs.getPlayer(), false);
    }
    
    public void cancel() {
        this._isActive = false;
        if (this._scheduler != null) {
            this._scheduler.cancel(false);
        }
    }
    
    public void cancelAndRemove() {
        this.cancel();
        this._quest.removeQuestTimer(this);
    }
    
    public boolean isMatch(final Quest quest, final String name, final Npc npc, final Player player) {
        return quest != null && name != null && quest == this._quest && name.equalsIgnoreCase(this._name) && npc == this._npc && player == this._player;
    }
    
    public final boolean getIsActive() {
        return this._isActive;
    }
    
    public final boolean getIsRepeating() {
        return this._isRepeating;
    }
    
    public final Quest getQuest() {
        return this._quest;
    }
    
    public final String getName() {
        return this._name;
    }
    
    public final Npc getNpc() {
        return this._npc;
    }
    
    public final Player getPlayer() {
        return this._player;
    }
    
    @Override
    public final String toString() {
        return this._name;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)QuestTimer.class);
    }
    
    public class ScheduleTimerTask implements Runnable
    {
        @Override
        public void run() {
            if (!QuestTimer.this._isActive) {
                return;
            }
            try {
                if (!QuestTimer.this._isRepeating) {
                    QuestTimer.this.cancelAndRemove();
                }
                QuestTimer.this._quest.notifyEvent(QuestTimer.this._name, QuestTimer.this._npc, QuestTimer.this._player);
            }
            catch (Exception e) {
                QuestTimer.LOGGER.error("", (Throwable)e);
            }
        }
    }
}
