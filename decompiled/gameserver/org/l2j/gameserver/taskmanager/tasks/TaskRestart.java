// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.Shutdown;
import org.l2j.gameserver.taskmanager.TaskManager;
import org.l2j.gameserver.taskmanager.Task;

public final class TaskRestart extends Task
{
    private static final String NAME = "restart";
    
    @Override
    public String getName() {
        return "restart";
    }
    
    @Override
    public void onTimeElapsed(final TaskManager.ExecutableTask task) {
        final Shutdown handler = new Shutdown(Integer.parseInt(task.getParam3()), true);
        handler.start();
    }
}
