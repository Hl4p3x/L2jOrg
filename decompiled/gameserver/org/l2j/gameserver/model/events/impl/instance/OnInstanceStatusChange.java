// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.instance;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnInstanceStatusChange implements IBaseEvent
{
    private final Instance _world;
    private final int _status;
    
    public OnInstanceStatusChange(final Instance world, final int status) {
        this._world = world;
        this._status = status;
    }
    
    public Instance getWorld() {
        return this._world;
    }
    
    public int getStatus() {
        return this._status;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_INSTANCE_STATUS_CHANGE;
    }
}
