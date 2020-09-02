// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

class TimerThread extends Thread
{
    private final String guid;
    private Scheduler scheduler;
    
    public TimerThread(final Scheduler scheduler) {
        this.guid = GUIDGenerator.generate();
        this.scheduler = scheduler;
        final String name = invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;, scheduler.getGuid(), this.guid);
        this.setName(name);
    }
    
    public Object getGuid() {
        return this.guid;
    }
    
    private void safeSleep(final long millis) throws InterruptedException {
        long done = 0L;
        do {
            final long before = System.currentTimeMillis();
            Thread.sleep(millis - done);
            final long after = System.currentTimeMillis();
            done += after - before;
        } while (done < millis);
    }
    
    @Override
    public void run() {
        long millis = System.currentTimeMillis();
        long nextMinute = (millis / 60000L + 1L) * 60000L;
        while (true) {
            final long sleepTime = nextMinute - System.currentTimeMillis();
            if (sleepTime > 0L) {
                try {
                    this.safeSleep(sleepTime);
                }
                catch (InterruptedException e) {
                    break;
                }
            }
            millis = System.currentTimeMillis();
            this.scheduler.spawnLauncher(millis);
            nextMinute = (millis / 60000L + 1L) * 60000L;
        }
        this.scheduler = null;
    }
}
