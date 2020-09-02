// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

class LauncherThread extends Thread
{
    private final String guid;
    private final Scheduler scheduler;
    private final TaskCollector[] collectors;
    private final long referenceTimeInMillis;
    
    public LauncherThread(final Scheduler scheduler, final TaskCollector[] collectors, final long referenceTimeInMillis) {
        this.guid = GUIDGenerator.generate();
        this.scheduler = scheduler;
        this.collectors = collectors;
        this.referenceTimeInMillis = referenceTimeInMillis;
        final String name = invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;, scheduler.getGuid(), this.guid);
        this.setName(name);
    }
    
    public Object getGuid() {
        return this.guid;
    }
    
    @Override
    public void run() {
    Label_0115:
        for (final TaskCollector collector : this.collectors) {
            final TaskTable taskTable = collector.getTasks();
            for (int size = taskTable.size(), j = 0; j < size; ++j) {
                if (this.isInterrupted()) {
                    break Label_0115;
                }
                final SchedulingPattern pattern = taskTable.getSchedulingPattern(j);
                if (pattern.match(this.scheduler.getTimeZone(), this.referenceTimeInMillis)) {
                    final Task task = taskTable.getTask(j);
                    this.scheduler.spawnExecutor(task);
                }
            }
        }
        this.scheduler.notifyLauncherCompleted(this);
    }
}
