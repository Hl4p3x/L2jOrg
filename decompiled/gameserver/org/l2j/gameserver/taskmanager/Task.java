// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import java.util.concurrent.ScheduledFuture;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class Task
{
    protected final Logger LOGGER;
    
    public Task() {
        this.LOGGER = LoggerFactory.getLogger(this.getClass().getName());
    }
    
    public void initializate() {
    }
    
    public ScheduledFuture<?> launchSpecial(final TaskManager.ExecutableTask instance) {
        return null;
    }
    
    public abstract String getName();
    
    public abstract void onTimeElapsed(final TaskManager.ExecutableTask task);
    
    public void onDestroy() {
    }
}
