// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.listeners;

import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import java.util.function.Consumer;

public class ConsumerEventListener extends AbstractEventListener
{
    private final Consumer<IBaseEvent> _callback;
    
    public ConsumerEventListener(final ListenersContainer container, final EventType type, final Consumer<? extends IBaseEvent> callback, final Object owner) {
        super(container, type, owner);
        this._callback = (Consumer<IBaseEvent>)callback;
    }
    
    @Override
    public <R extends AbstractEventReturn> R executeEvent(final IBaseEvent event, final Class<R> returnBackClass) {
        this._callback.accept(event);
        return null;
    }
}
