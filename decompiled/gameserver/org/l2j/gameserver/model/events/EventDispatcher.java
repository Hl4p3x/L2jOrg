// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.Queue;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.slf4j.Logger;

public final class EventDispatcher
{
    private static final Logger LOGGER;
    
    private EventDispatcher() {
    }
    
    public <T extends AbstractEventReturn> T notifyEvent(final IBaseEvent event) {
        return this.notifyEvent(event, null, (Class<T>)null);
    }
    
    public <T extends AbstractEventReturn> T notifyEvent(final IBaseEvent event, final Class<T> callbackClass) {
        return this.notifyEvent(event, null, callbackClass);
    }
    
    public <T extends AbstractEventReturn> T notifyEvent(final IBaseEvent event, final ListenersContainer container) {
        return this.notifyEvent(event, container, (Class<T>)null);
    }
    
    public <T extends AbstractEventReturn> T notifyEvent(final IBaseEvent event, final ListenersContainer container, final Class<T> callbackClass) {
        try {
            return (T)((Listeners.Global().hasListener(event.getType()) || (container != null && container.hasListener(event.getType()))) ? this.notifyEventImpl(event, container, (Class<AbstractEventReturn>)callbackClass) : null);
        }
        catch (Exception e) {
            EventDispatcher.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), event.getClass().getSimpleName()), (Throwable)e);
            return null;
        }
    }
    
    public void notifyEventAsync(final IBaseEvent event, final ListenersContainer... containers) {
        if (event == null) {
            throw new NullPointerException("Event cannot be null!");
        }
        boolean hasListeners = Listeners.Global().hasListener(event.getType());
        if (!hasListeners) {
            for (final ListenersContainer container : containers) {
                if (container.hasListener(event.getType())) {
                    hasListeners = true;
                    break;
                }
            }
        }
        if (hasListeners) {
            ThreadPool.execute(() -> this.notifyEventToMultipleContainers(event, containers, (Class<AbstractEventReturn>)null));
        }
    }
    
    public void notifyEventAsyncDelayed(final IBaseEvent event, final ListenersContainer container, final long delay) {
        if (Listeners.Global().hasListener(event.getType()) || container.hasListener(event.getType())) {
            ThreadPool.schedule(() -> this.notifyEvent(event, container, (Class<AbstractEventReturn>)null), delay);
        }
    }
    
    private <T extends AbstractEventReturn> T notifyEventToMultipleContainers(final IBaseEvent event, final ListenersContainer[] containers, final Class<T> callbackClass) {
        try {
            if (event == null) {
                throw new NullPointerException("Event cannot be null!");
            }
            T callback = null;
            if (containers != null) {
                for (final ListenersContainer container : containers) {
                    if (callback == null || !callback.abort()) {
                        callback = this.notifyToListeners(container.getListeners(event.getType()), event, callbackClass, callback);
                    }
                }
            }
            if (callback == null || !callback.abort()) {
                callback = this.notifyToListeners(Listeners.Global().getListeners(event.getType()), event, callbackClass, callback);
            }
            return callback;
        }
        catch (Exception e) {
            EventDispatcher.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), event.getClass().getSimpleName()), (Throwable)e);
            return null;
        }
    }
    
    private <T extends AbstractEventReturn> T notifyEventImpl(final IBaseEvent event, final ListenersContainer container, final Class<T> callbackClass) {
        if (event == null) {
            throw new NullPointerException("Event cannot be null!");
        }
        T callback = null;
        if (container != null) {
            callback = this.notifyToListeners(container.getListeners(event.getType()), event, callbackClass, callback);
        }
        if (callback == null || !callback.abort()) {
            callback = this.notifyToListeners(Listeners.Global().getListeners(event.getType()), event, callbackClass, callback);
        }
        return callback;
    }
    
    private <T extends AbstractEventReturn> T notifyToListeners(final Queue<AbstractEventListener> listeners, final IBaseEvent event, final Class<T> returnBackClass, T callback) {
        for (final AbstractEventListener listener : listeners) {
            try {
                final T rb = listener.executeEvent(event, returnBackClass);
                if (rb == null) {
                    continue;
                }
                if (callback == null || rb.override()) {
                    callback = rb;
                }
                else {
                    if (rb.abort()) {
                        break;
                    }
                    continue;
                }
            }
            catch (Exception e) {
                EventDispatcher.LOGGER.warn("Exception during notification of event: {} listener {} : {}", new Object[] { event.getClass().getSimpleName(), listener.getClass().getSimpleName(), e.getCause() });
            }
        }
        return callback;
    }
    
    public static EventDispatcher getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)EventDispatcher.class);
    }
    
    private static class Singleton
    {
        private static final EventDispatcher INSTANCE;
        
        static {
            INSTANCE = new EventDispatcher();
        }
    }
}
