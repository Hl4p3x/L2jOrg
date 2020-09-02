// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.effects;

import java.util.concurrent.ScheduledFuture;

public class EffectTaskInfo
{
    private final EffectTickTask _effectTask;
    private final ScheduledFuture<?> _scheduledFuture;
    
    public EffectTaskInfo(final EffectTickTask effectTask, final ScheduledFuture<?> scheduledFuture) {
        this._effectTask = effectTask;
        this._scheduledFuture = scheduledFuture;
    }
    
    public EffectTickTask getEffectTask() {
        return this._effectTask;
    }
    
    public ScheduledFuture<?> getScheduledFuture() {
        return this._scheduledFuture;
    }
}
