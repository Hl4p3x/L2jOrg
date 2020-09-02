// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.listeners;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import java.util.function.Function;
import org.slf4j.Logger;

public class FunctionEventListener extends AbstractEventListener
{
    private static final Logger LOGGER;
    private final Function<IBaseEvent, ? extends AbstractEventReturn> _callback;
    
    public FunctionEventListener(final ListenersContainer container, final EventType type, final Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, final Object owner) {
        super(container, type, owner);
        this._callback = (Function<IBaseEvent, ? extends AbstractEventReturn>)callback;
    }
    
    @Override
    public <R extends AbstractEventReturn> R executeEvent(final IBaseEvent event, final Class<R> returnBackClass) {
        try {
            return returnBackClass.cast(this._callback.apply(event));
        }
        catch (Exception e) {
            FunctionEventListener.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/events/impl/IBaseEvent;Ljava/lang/Object;)Ljava/lang/String;, this.getClass().getSimpleName(), event, this.getOwner()), (Throwable)e);
            return null;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)FunctionEventListener.class);
    }
}
