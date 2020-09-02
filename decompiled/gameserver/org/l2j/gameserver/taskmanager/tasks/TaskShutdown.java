// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.Shutdown;
import org.l2j.gameserver.taskmanager.TaskManager;
import org.l2j.gameserver.taskmanager.Task;

public class TaskShutdown extends Task
{
    private static final String NAME = "shutdown";
    
    @Override
    public String getName() {
        return "shutdown";
    }
    
    @Override
    public void onTimeElapsed(final TaskManager.ExecutableTask task) {
        final Shutdown handler = new Shutdown(Integer.parseInt(task.getParam3()), false);
        handler.start();
    }
}
