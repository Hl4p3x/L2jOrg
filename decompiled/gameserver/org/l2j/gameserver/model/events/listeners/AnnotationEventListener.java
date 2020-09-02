// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.listeners;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenersContainer;
import java.lang.reflect.Method;
import org.slf4j.Logger;

public class AnnotationEventListener extends AbstractEventListener
{
    private static final Logger LOGGER;
    private final Method _callback;
    
    public AnnotationEventListener(final ListenersContainer container, final EventType type, final Method callback, final Object owner, final int priority) {
        super(container, type, owner);
        this._callback = callback;
        this.setPriority(priority);
    }
    
    @Override
    public <R extends AbstractEventReturn> R executeEvent(final IBaseEvent event, final Class<R> returnBackClass) {
        try {
            final Object result = this._callback.invoke(this.getOwner(), event);
            if (this._callback.getReturnType() == returnBackClass) {
                return returnBackClass.cast(result);
            }
        }
        catch (Exception e) {
            AnnotationEventListener.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, this.getClass().getSimpleName(), this._callback.getName(), this.getOwner()), (Throwable)e);
        }
        return null;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AnnotationEventListener.class);
    }
}
