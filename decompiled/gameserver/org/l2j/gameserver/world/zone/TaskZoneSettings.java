// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone;

import java.util.concurrent.Future;

public class TaskZoneSettings extends AbstractZoneSettings
{
    private Future<?> _task;
    
    public Future<?> getTask() {
        return this._task;
    }
    
    public void setTask(final Future<?> task) {
        this._task = task;
    }
    
    @Override
    public void clear() {
        if (this._task != null) {
            this._task.cancel(true);
            this._task = null;
        }
    }
}
