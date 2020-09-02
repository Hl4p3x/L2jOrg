// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.instance;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnInstanceDestroy implements IBaseEvent
{
    private final Instance _instance;
    
    public OnInstanceDestroy(final Instance instance) {
        this._instance = instance;
    }
    
    public Instance getInstanceWorld() {
        return this._instance;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_INSTANCE_DESTROY;
    }
}
