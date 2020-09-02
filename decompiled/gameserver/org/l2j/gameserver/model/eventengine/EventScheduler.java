// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.eventengine;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.EventDAO;
import org.l2j.commons.threading.ThreadPool;
import java.util.ArrayList;
import org.l2j.gameserver.util.cron4j.PastPredictor;
import org.l2j.gameserver.util.cron4j.Predictor;
import org.l2j.gameserver.model.StatsSet;
import java.util.concurrent.ScheduledFuture;
import java.util.List;
import org.slf4j.Logger;

public class EventScheduler
{
    private static final Logger LOGGER;
    private final AbstractEventManager<?> eventManager;
    private final String name;
    private final String _pattern;
    private final boolean _repeat;
    private List<EventMethodNotification> notifications;
    private ScheduledFuture<?> _task;
    private long lastRun;
    
    public EventScheduler(final AbstractEventManager<?> manager, final StatsSet set) {
        this.lastRun = 0L;
        this.eventManager = manager;
        this.name = set.getString("name", "");
        this._pattern = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, set.getString("minute", "*"), set.getString("hour", "*"), set.getString("dayOfMonth", "*"), set.getString("month", "*"), set.getString("dayOfWeek", "*"));
        this._repeat = set.getBoolean("repeat", false);
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getNextSchedule() {
        final Predictor predictor = new Predictor(this._pattern);
        return predictor.nextMatchingTime();
    }
    
    public long getNextSchedule(final long fromTime) {
        final Predictor predictor = new Predictor(this._pattern, fromTime);
        return predictor.nextMatchingTime();
    }
    
    public long getPrevSchedule() {
        final PastPredictor predictor = new PastPredictor(this._pattern);
        return predictor.prevMatchingTime();
    }
    
    public long getPrevSchedule(final long fromTime) {
        final PastPredictor predictor = new PastPredictor(this._pattern, fromTime);
        return predictor.prevMatchingTime();
    }
    
    public boolean isRepeating() {
        return this._repeat;
    }
    
    public void addEventNotification(final EventMethodNotification notification) {
        if (this.notifications == null) {
            this.notifications = new ArrayList<EventMethodNotification>();
        }
        this.notifications.add(notification);
    }
    
    public List<EventMethodNotification> getEventNotifications() {
        return this.notifications;
    }
    
    public void startScheduler() {
        if (this.notifications == null) {
            EventScheduler.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.eventManager.getClass().getSimpleName(), this._pattern));
            return;
        }
        final Predictor predictor = new Predictor(this._pattern);
        final long nextSchedule = predictor.nextMatchingTime();
        final long timeSchedule = nextSchedule - System.currentTimeMillis();
        if (timeSchedule <= 30000L) {
            EventScheduler.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;J)Ljava/lang/String;, this.eventManager.getClass().getSimpleName(), timeSchedule / 1000L));
            ThreadPool.schedule(this::startScheduler, timeSchedule + 1000L);
            return;
        }
        if (this._task != null) {
            this._task.cancel(false);
        }
        this._task = (ScheduledFuture<?>)ThreadPool.schedule(() -> {
            this.run();
            this.updateLastRun();
            if (this._repeat) {
                ThreadPool.schedule(this::startScheduler, 1000L);
            }
        }, timeSchedule);
    }
    
    public boolean updateLastRun() {
        this.lastRun = System.currentTimeMillis();
        ((EventDAO)DatabaseAccess.getDAO((Class)EventDAO.class)).updateLastRun(this.eventManager.getName(), this.name, this.lastRun);
        return true;
    }
    
    public void stopScheduler() {
        if (this._task != null) {
            this._task.cancel(false);
            this._task = null;
        }
    }
    
    public long getRemainingTime(final TimeUnit unit) {
        return (this._task != null && !this._task.isDone()) ? this._task.getDelay(unit) : 0L;
    }
    
    public long getLastRun() {
        return this.lastRun;
    }
    
    public void run() {
        for (final EventMethodNotification notification : this.notifications) {
            try {
                notification.execute();
            }
            catch (Exception e) {
                EventScheduler.LOGGER.warn("Failed to notify to event manager: {} method: {}", (Object)notification.getManager().getClass(), (Object)notification.getMethod().getName());
                EventScheduler.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EventScheduler.class);
    }
}
