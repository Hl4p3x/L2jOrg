// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.ArrayList;

public class TaskTable
{
    private final ArrayList<SchedulingPattern> patterns;
    private final ArrayList<Task> tasks;
    private int size;
    
    public TaskTable() {
        this.patterns = new ArrayList<SchedulingPattern>();
        this.tasks = new ArrayList<Task>();
        this.size = 0;
    }
    
    public void add(final SchedulingPattern pattern, final Task task) {
        this.patterns.add(pattern);
        this.tasks.add(task);
        ++this.size;
    }
    
    public int size() {
        return this.size;
    }
    
    public Task getTask(final int index) throws IndexOutOfBoundsException {
        return this.tasks.get(index);
    }
    
    public SchedulingPattern getSchedulingPattern(final int index) throws IndexOutOfBoundsException {
        return this.patterns.get(index);
    }
    
    public void remove(final int index) throws IndexOutOfBoundsException {
        this.tasks.remove(index);
        this.patterns.remove(index);
        --this.size;
    }
}
