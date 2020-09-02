// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.Iterator;
import java.util.ArrayList;

public class TaskExecutor
{
    final Scheduler scheduler;
    final Task task;
    final MyContext context;
    final TaskExecutor myself;
    final Object lock;
    private final String guid;
    private final ArrayList<TaskExecutorListener> listeners;
    long startTime;
    boolean paused;
    boolean stopped;
    private Thread thread;
    
    TaskExecutor(final Scheduler scheduler, final Task task) {
        this.myself = this;
        this.lock = new Object();
        this.guid = GUIDGenerator.generate();
        this.listeners = new ArrayList<TaskExecutorListener>();
        this.startTime = -1L;
        this.paused = false;
        this.stopped = false;
        this.scheduler = scheduler;
        this.task = task;
        this.context = new MyContext();
    }
    
    public void addTaskExecutorListener(final TaskExecutorListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }
    
    public void removeTaskExecutorListener(final TaskExecutorListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }
    
    public TaskExecutorListener[] getTaskExecutorListeners() {
        synchronized (this.listeners) {
            final int size = this.listeners.size();
            final TaskExecutorListener[] ret = new TaskExecutorListener[size];
            for (int i = 0; i < size; ++i) {
                ret[i] = this.listeners.get(i);
            }
            return ret;
        }
    }
    
    public String getGuid() {
        return this.guid;
    }
    
    public Scheduler getScheduler() {
        return this.scheduler;
    }
    
    public Task getTask() {
        return this.task;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public boolean canBePaused() {
        return this.task.canBePaused();
    }
    
    public boolean canBeStopped() {
        return this.task.canBeStopped();
    }
    
    public boolean supportsCompletenessTracking() {
        return this.task.supportsCompletenessTracking();
    }
    
    public boolean supportsStatusTracking() {
        return this.task.supportsStatusTracking();
    }
    
    void start(final boolean daemon) {
        synchronized (this.lock) {
            this.startTime = System.currentTimeMillis();
            final String name = invokedynamic(makeConcatWithConstants:(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;, this.scheduler.getGuid(), this.guid);
            (this.thread = new Thread(new Runner())).setDaemon(daemon);
            this.thread.setName(name);
            this.thread.start();
        }
    }
    
    public void pause() throws UnsupportedOperationException {
        if (!this.task.canBePaused()) {
            throw new UnsupportedOperationException("Pause not supported");
        }
        synchronized (this.lock) {
            if (this.thread != null && !this.paused) {
                this.notifyExecutionPausing();
                this.paused = true;
            }
        }
    }
    
    private void resume() {
        synchronized (this.lock) {
            if (this.thread != null && this.paused) {
                this.notifyExecutionResuming();
                this.paused = false;
                this.lock.notifyAll();
            }
        }
    }
    
    public void stop() throws UnsupportedOperationException {
        if (!this.task.canBeStopped()) {
            throw new UnsupportedOperationException("Stop not supported");
        }
        boolean joinit = false;
        synchronized (this.lock) {
            if (this.thread != null && !this.stopped) {
                this.stopped = true;
                if (this.paused) {
                    this.resume();
                }
                this.notifyExecutionStopping();
                this.thread.interrupt();
                joinit = true;
            }
        }
        if (joinit) {
            while (true) {
                try {
                    this.thread.join();
                }
                catch (InterruptedException e) {
                    continue;
                }
                break;
            }
            this.thread = null;
        }
    }
    
    public void join() throws InterruptedException {
        if (this.thread != null) {
            this.thread.join();
        }
    }
    
    public boolean isAlive() {
        return this.thread != null && this.thread.isAlive();
    }
    
    public String getStatusMessage() throws UnsupportedOperationException {
        if (!this.task.supportsStatusTracking()) {
            throw new UnsupportedOperationException("Status tracking not supported");
        }
        return this.context.getStatusMessage();
    }
    
    public double getCompleteness() throws UnsupportedOperationException {
        if (!this.task.supportsCompletenessTracking()) {
            throw new UnsupportedOperationException("Completeness tracking not supported");
        }
        return this.context.getCompleteness();
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public boolean isStopped() {
        return this.stopped;
    }
    
    private void notifyExecutionPausing() {
        synchronized (this.listeners) {
            for (final TaskExecutorListener l : this.listeners) {
                final TaskExecutorListener taskExecutorListener = l;
                l.executionPausing(this);
            }
        }
    }
    
    private void notifyExecutionResuming() {
        synchronized (this.listeners) {
            for (final TaskExecutorListener l : this.listeners) {
                final TaskExecutorListener taskExecutorListener = l;
                l.executionResuming(this);
            }
        }
    }
    
    private void notifyExecutionStopping() {
        synchronized (this.listeners) {
            for (final TaskExecutorListener l : this.listeners) {
                final TaskExecutorListener taskExecutorListener = l;
                l.executionStopping(this);
            }
        }
    }
    
    void notifyExecutionTerminated(final Throwable exception) {
        synchronized (this.listeners) {
            for (final TaskExecutorListener l : this.listeners) {
                final TaskExecutorListener taskExecutorListener = l;
                l.executionTerminated(this, exception);
            }
        }
    }
    
    void notifyStatusMessageChanged(final String statusMessage) {
        synchronized (this.listeners) {
            for (final TaskExecutorListener l : this.listeners) {
                final TaskExecutorListener taskExecutorListener = l;
                l.statusMessageChanged(this, statusMessage);
            }
        }
    }
    
    void notifyCompletenessValueChanged(final double completenessValue) {
        synchronized (this.listeners) {
            for (final TaskExecutorListener l : this.listeners) {
                final TaskExecutorListener taskExecutorListener = l;
                l.completenessValueChanged(this, completenessValue);
            }
        }
    }
    
    private class Runner implements Runnable
    {
        public Runner() {
        }
        
        @Override
        public void run() {
            Throwable error = null;
            TaskExecutor.this.startTime = System.currentTimeMillis();
            try {
                TaskExecutor.this.scheduler.notifyTaskLaunching(TaskExecutor.this.myself);
                TaskExecutor.this.task.execute(TaskExecutor.this.context);
                TaskExecutor.this.scheduler.notifyTaskSucceeded(TaskExecutor.this.myself);
            }
            catch (Throwable exception) {
                error = exception;
                TaskExecutor.this.scheduler.notifyTaskFailed(TaskExecutor.this.myself, exception);
            }
            finally {
                TaskExecutor.this.notifyExecutionTerminated(error);
                TaskExecutor.this.scheduler.notifyExecutorCompleted(TaskExecutor.this.myself);
            }
        }
    }
    
    private class MyContext implements TaskExecutionContext
    {
        private String message;
        private double completeness;
        
        public MyContext() {
            this.message = "";
            this.completeness = 0.0;
        }
        
        @Override
        public Scheduler getScheduler() {
            return TaskExecutor.this.scheduler;
        }
        
        @Override
        public TaskExecutor getTaskExecutor() {
            return TaskExecutor.this.myself;
        }
        
        @Override
        public boolean isStopped() {
            return TaskExecutor.this.stopped;
        }
        
        @Override
        public void pauseIfRequested() {
            synchronized (TaskExecutor.this.lock) {
                if (TaskExecutor.this.paused) {
                    try {
                        TaskExecutor.this.lock.wait();
                    }
                    catch (InterruptedException ex) {}
                }
            }
        }
        
        public String getStatusMessage() {
            return this.message;
        }
        
        @Override
        public void setStatusMessage(final String message) {
            this.message = ((message != null) ? message : "");
            TaskExecutor.this.notifyStatusMessageChanged(message);
        }
        
        public double getCompleteness() {
            return this.completeness;
        }
        
        @Override
        public void setCompleteness(final double completeness) {
            if (completeness >= 0.0 && completeness <= 1.0) {
                this.completeness = completeness;
                TaskExecutor.this.notifyCompletenessValueChanged(completeness);
            }
        }
    }
}
