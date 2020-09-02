// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.io.File;
import java.util.TimeZone;
import java.util.ArrayList;

public class Scheduler
{
    private final String guid;
    private final ArrayList<TaskCollector> collectors;
    private final MemoryTaskCollector memoryTaskCollector;
    private final FileTaskCollector fileTaskCollector;
    private final ArrayList<SchedulerListener> listeners;
    private final Object lock;
    private TimeZone timezone;
    private boolean daemon;
    private boolean started;
    private TimerThread timer;
    private ArrayList<LauncherThread> launchers;
    private ArrayList<TaskExecutor> executors;
    
    public Scheduler() {
        this.guid = GUIDGenerator.generate();
        this.collectors = new ArrayList<TaskCollector>();
        this.memoryTaskCollector = new MemoryTaskCollector();
        this.fileTaskCollector = new FileTaskCollector();
        this.listeners = new ArrayList<SchedulerListener>();
        this.lock = new Object();
        this.timezone = null;
        this.daemon = false;
        this.started = false;
        this.timer = null;
        this.launchers = null;
        this.executors = null;
        this.collectors.add(this.memoryTaskCollector);
        this.collectors.add(this.fileTaskCollector);
    }
    
    public Object getGuid() {
        return this.guid;
    }
    
    public TimeZone getTimeZone() {
        return (this.timezone != null) ? this.timezone : TimeZone.getDefault();
    }
    
    public void setTimeZone(final TimeZone timezone) {
        this.timezone = timezone;
    }
    
    public boolean isDaemon() {
        return this.daemon;
    }
    
    public void setDaemon(final boolean on) throws IllegalStateException {
        synchronized (this.lock) {
            if (this.started) {
                throw new IllegalStateException("Scheduler already started");
            }
            this.daemon = on;
        }
    }
    
    public boolean isStarted() {
        synchronized (this.lock) {
            return this.started;
        }
    }
    
    public void scheduleFile(final File file) {
        this.fileTaskCollector.addFile(file);
    }
    
    public void descheduleFile(final File file) {
        this.fileTaskCollector.removeFile(file);
    }
    
    public File[] getScheduledFiles() {
        return this.fileTaskCollector.getFiles();
    }
    
    public void addTaskCollector(final TaskCollector collector) {
        synchronized (this.collectors) {
            this.collectors.add(collector);
        }
    }
    
    public void removeTaskCollector(final TaskCollector collector) {
        synchronized (this.collectors) {
            this.collectors.remove(collector);
        }
    }
    
    public TaskCollector[] getTaskCollectors() {
        synchronized (this.collectors) {
            final int size = this.collectors.size() - 2;
            final TaskCollector[] ret = new TaskCollector[size];
            for (int i = 0; i < size; ++i) {
                ret[i] = this.collectors.get(i + 2);
            }
            return ret;
        }
    }
    
    public void addSchedulerListener(final SchedulerListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }
    
    public void removeSchedulerListener(final SchedulerListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }
    
    public SchedulerListener[] getSchedulerListeners() {
        synchronized (this.listeners) {
            final int size = this.listeners.size();
            final SchedulerListener[] ret = new SchedulerListener[size];
            for (int i = 0; i < size; ++i) {
                ret[i] = this.listeners.get(i);
            }
            return ret;
        }
    }
    
    public TaskExecutor[] getExecutingTasks() {
        synchronized (this.executors) {
            final int size = this.executors.size();
            final TaskExecutor[] ret = new TaskExecutor[size];
            for (int i = 0; i < size; ++i) {
                ret[i] = this.executors.get(i);
            }
            return ret;
        }
    }
    
    public String schedule(final String schedulingPattern, final Runnable task) throws InvalidPatternException {
        return this.schedule(schedulingPattern, new RunnableTask(task));
    }
    
    public String schedule(final String schedulingPattern, final Task task) throws InvalidPatternException {
        return this.schedule(new SchedulingPattern(schedulingPattern), task);
    }
    
    public String schedule(final SchedulingPattern schedulingPattern, final Task task) {
        return this.memoryTaskCollector.add(schedulingPattern, task);
    }
    
    @Deprecated
    public void reschedule(final Object id, final String schedulingPattern) throws InvalidPatternException {
        this.reschedule((String)id, new SchedulingPattern(schedulingPattern));
    }
    
    public void reschedule(final String id, final String schedulingPattern) throws InvalidPatternException {
        this.reschedule(id, new SchedulingPattern(schedulingPattern));
    }
    
    public void reschedule(final String id, final SchedulingPattern schedulingPattern) {
        this.memoryTaskCollector.update(id, schedulingPattern);
    }
    
    @Deprecated
    public void deschedule(final Object id) {
        this.deschedule((String)id);
    }
    
    public void deschedule(final String id) {
        this.memoryTaskCollector.remove(id);
    }
    
    public Task getTask(final String id) {
        return this.memoryTaskCollector.getTask(id);
    }
    
    public SchedulingPattern getSchedulingPattern(final String id) {
        return this.memoryTaskCollector.getSchedulingPattern(id);
    }
    
    @Deprecated
    public Runnable getTaskRunnable(final Object id) {
        final Task task = this.getTask((String)id);
        if (task instanceof RunnableTask) {
            final RunnableTask rt = (RunnableTask)task;
            return rt.getRunnable();
        }
        return null;
    }
    
    @Deprecated
    public String getTaskSchedulingPattern(final Object id) {
        return this.getSchedulingPattern((String)id).toString();
    }
    
    public TaskExecutor launch(final Task task) {
        synchronized (this.lock) {
            if (!this.started) {
                throw new IllegalStateException("Scheduler not started");
            }
            return this.spawnExecutor(task);
        }
    }
    
    public void start() throws IllegalStateException {
        synchronized (this.lock) {
            if (this.started) {
                throw new IllegalStateException("Scheduler already started");
            }
            this.launchers = new ArrayList<LauncherThread>();
            this.executors = new ArrayList<TaskExecutor>();
            (this.timer = new TimerThread(this)).setDaemon(this.daemon);
            this.timer.start();
            this.started = true;
        }
    }
    
    public void stop() throws IllegalStateException {
        synchronized (this.lock) {
            if (!this.started) {
                throw new IllegalStateException("Scheduler not started");
            }
            this.timer.interrupt();
            this.tillThreadDies(this.timer);
            this.timer = null;
            while (true) {
                LauncherThread launcher = null;
                synchronized (this.launchers) {
                    if (this.launchers.size() == 0) {
                        break;
                    }
                    launcher = this.launchers.remove(0);
                }
                launcher.interrupt();
                this.tillThreadDies(launcher);
            }
            this.launchers = null;
            while (true) {
                TaskExecutor executor = null;
                synchronized (this.executors) {
                    if (this.executors.size() == 0) {
                        break;
                    }
                    executor = this.executors.remove(0);
                }
                if (executor.canBeStopped()) {
                    executor.stop();
                }
                this.tillExecutorDies(executor);
            }
            this.executors = null;
            this.started = false;
        }
    }
    
    LauncherThread spawnLauncher(final long referenceTimeInMillis) {
        final TaskCollector[] nowCollectors;
        synchronized (this.collectors) {
            final int size = this.collectors.size();
            nowCollectors = new TaskCollector[size];
            for (int i = 0; i < size; ++i) {
                nowCollectors[i] = this.collectors.get(i);
            }
        }
        final LauncherThread l = new LauncherThread(this, nowCollectors, referenceTimeInMillis);
        synchronized (this.launchers) {
            this.launchers.add(l);
        }
        l.setDaemon(this.daemon);
        l.start();
        return l;
    }
    
    TaskExecutor spawnExecutor(final Task task) {
        final TaskExecutor e = new TaskExecutor(this, task);
        synchronized (this.executors) {
            this.executors.add(e);
        }
        e.start(this.daemon);
        return e;
    }
    
    void notifyLauncherCompleted(final LauncherThread launcher) {
        synchronized (this.launchers) {
            this.launchers.remove(launcher);
        }
    }
    
    void notifyExecutorCompleted(final TaskExecutor executor) {
        synchronized (this.executors) {
            this.executors.remove(executor);
        }
    }
    
    void notifyTaskLaunching(final TaskExecutor executor) {
        synchronized (this.listeners) {
            for (int size = this.listeners.size(), i = 0; i < size; ++i) {
                final SchedulerListener l = this.listeners.get(i);
                l.taskLaunching(executor);
            }
        }
    }
    
    void notifyTaskSucceeded(final TaskExecutor executor) {
        synchronized (this.listeners) {
            for (int size = this.listeners.size(), i = 0; i < size; ++i) {
                final SchedulerListener l = this.listeners.get(i);
                l.taskSucceeded(executor);
            }
        }
    }
    
    void notifyTaskFailed(final TaskExecutor executor, final Throwable exception) {
        synchronized (this.listeners) {
            final int size = this.listeners.size();
            if (size > 0) {
                for (int i = 0; i < size; ++i) {
                    final SchedulerListener l = this.listeners.get(i);
                    l.taskFailed(executor, exception);
                }
            }
            else {
                exception.printStackTrace();
            }
        }
    }
    
    private void tillThreadDies(final Thread thread) {
        boolean dead = false;
        do {
            try {
                thread.join();
                dead = true;
            }
            catch (InterruptedException ex) {}
        } while (!dead);
    }
    
    private void tillExecutorDies(final TaskExecutor executor) {
        boolean dead = false;
        do {
            try {
                executor.join();
                dead = true;
            }
            catch (InterruptedException ex) {}
        } while (!dead);
    }
}
