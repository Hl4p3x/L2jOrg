// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.instance;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnInstanceCreated implements IBaseEvent
{
    private final Instance _instance;
    private final Player _creator;
    
    public OnInstanceCreated(final Instance instance, final Player creator) {
        this._instance = instance;
        this._creator = creator;
    }
    
    public Instance getInstanceWorld() {
        return this._instance;
    }
    
    public Player getCreator() {
        return this._creator;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_INSTANCE_CREATED;
    }
}
