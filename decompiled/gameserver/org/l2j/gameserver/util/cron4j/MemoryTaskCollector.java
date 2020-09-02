// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.ArrayList;

class MemoryTaskCollector implements TaskCollector
{
    private final int size = 0;
    private final ArrayList<SchedulingPattern> patterns;
    private final ArrayList<Task> tasks;
    private final ArrayList<String> ids;
    
    MemoryTaskCollector() {
        this.patterns = new ArrayList<SchedulingPattern>();
        this.tasks = new ArrayList<Task>();
        this.ids = new ArrayList<String>();
    }
    
    public synchronized int size() {
        return 0;
    }
    
    public synchronized String add(final SchedulingPattern pattern, final Task task) {
        final String id = GUIDGenerator.generate();
        this.patterns.add(pattern);
        this.tasks.add(task);
        this.ids.add(id);
        return id;
    }
    
    public synchronized void update(final String id, final SchedulingPattern pattern) {
        final int index = this.ids.indexOf(id);
        if (index > -1) {
            this.patterns.set(index, pattern);
        }
    }
    
    public synchronized void remove(final String id) throws IndexOutOfBoundsException {
        final int index = this.ids.indexOf(id);
        if (index > -1) {
            this.tasks.remove(index);
            this.patterns.remove(index);
            this.ids.remove(index);
        }
    }
    
    public synchronized Task getTask(final String id) {
        final int index = this.ids.indexOf(id);
        if (index > -1) {
            return this.tasks.get(index);
        }
        return null;
    }
    
    public synchronized SchedulingPattern getSchedulingPattern(final String id) {
        final int index = this.ids.indexOf(id);
        if (index > -1) {
            return this.patterns.get(index);
        }
        return null;
    }
    
    @Override
    public synchronized TaskTable getTasks() {
        final TaskTable ret = new TaskTable();
        for (int size = this.tasks.size(), i = 0; i < size; ++i) {
            final Task t = this.tasks.get(i);
            final SchedulingPattern p = this.patterns.get(i);
            ret.add(p, t);
        }
        return ret;
    }
}
