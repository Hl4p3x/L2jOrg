// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.listeners;

import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenersContainer;

public class RunnableEventListener extends AbstractEventListener
{
    private final Runnable _callback;
    
    public RunnableEventListener(final ListenersContainer container, final EventType type, final Runnable callback, final Object owner) {
        super(container, type, owner);
        this._callback = callback;
    }
    
    @Override
    public <R extends AbstractEventReturn> R executeEvent(final IBaseEvent event, final Class<R> returnBackClass) {
        this._callback.run();
        return null;
    }
}
