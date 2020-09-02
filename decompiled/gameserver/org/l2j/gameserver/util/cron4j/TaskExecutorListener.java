// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

public interface TaskExecutorListener
{
    void executionPausing(final TaskExecutor executor);
    
    void executionResuming(final TaskExecutor executor);
    
    void executionStopping(final TaskExecutor executor);
    
    void executionTerminated(final TaskExecutor executor, final Throwable exception);
    
    void statusMessageChanged(final TaskExecutor executor, final String statusMessage);
    
    void completenessValueChanged(final TaskExecutor executor, final double completenessValue);
}
