// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.commons.threading.ThreadPool;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.instance.Player;

public abstract class AbstractRequest
{
    private volatile long timestamp;
    private volatile boolean isProcessing;
    protected final Player player;
    private ScheduledFuture<?> timeOutTask;
    
    public AbstractRequest(final Player player) {
        this.timestamp = 0L;
        Objects.requireNonNull(player);
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    public void scheduleTimeout(final long delay) {
        this.timeOutTask = (ScheduledFuture<?>)ThreadPool.schedule(this::onTimeout, delay);
    }
    
    public boolean isTimeout() {
        return this.timeOutTask != null && !this.timeOutTask.isDone();
    }
    
    public void cancelTimeout() {
        if (Objects.nonNull(this.timeOutTask)) {
            this.timeOutTask.cancel(false);
        }
    }
    
    public boolean isProcessing() {
        return this.isProcessing;
    }
    
    public void setProcessing(final boolean isProcessing) {
        this.isProcessing = isProcessing;
    }
    
    public boolean canWorkWith(final AbstractRequest request) {
        return true;
    }
    
    public boolean isItemRequest() {
        return false;
    }
    
    public abstract boolean isUsingItem(final int objectId);
    
    public void onTimeout() {
    }
}
