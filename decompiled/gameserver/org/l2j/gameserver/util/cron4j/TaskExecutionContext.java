// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

public interface TaskExecutionContext
{
    Scheduler getScheduler();
    
    TaskExecutor getTaskExecutor();
    
    void setStatusMessage(final String message);
    
    void setCompleteness(final double completeness);
    
    void pauseIfRequested();
    
    boolean isStopped();
}
