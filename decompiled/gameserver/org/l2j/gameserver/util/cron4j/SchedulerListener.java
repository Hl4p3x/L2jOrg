// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

public interface SchedulerListener
{
    void taskLaunching(final TaskExecutor executor);
    
    void taskSucceeded(final TaskExecutor executor);
    
    void taskFailed(final TaskExecutor executor, final Throwable exception);
}
