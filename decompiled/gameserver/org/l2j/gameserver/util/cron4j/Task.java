// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

public abstract class Task
{
    private final Object id;
    
    public Task() {
        this.id = GUIDGenerator.generate();
    }
    
    Object getId() {
        return this.id;
    }
    
    public boolean canBePaused() {
        return false;
    }
    
    public boolean canBeStopped() {
        return false;
    }
    
    public boolean supportsStatusTracking() {
        return false;
    }
    
    public boolean supportsCompletenessTracking() {
        return false;
    }
    
    public abstract void execute(final TaskExecutionContext context) throws RuntimeException;
}
