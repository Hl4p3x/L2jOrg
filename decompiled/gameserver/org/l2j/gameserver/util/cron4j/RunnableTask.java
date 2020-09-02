// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

class RunnableTask extends Task
{
    private final Runnable runnable;
    
    public RunnableTask(final Runnable runnable) throws InvalidPatternException {
        this.runnable = runnable;
    }
    
    public Runnable getRunnable() {
        return this.runnable;
    }
    
    @Override
    public void execute(final TaskExecutionContext context) {
        this.runnable.run();
    }
    
    @Override
    public String toString() {
        final StringBuffer b = new StringBuffer();
        b.append("Task[");
        b.append("runnable=");
        b.append(this.runnable);
        b.append("]");
        return b.toString();
    }
}
