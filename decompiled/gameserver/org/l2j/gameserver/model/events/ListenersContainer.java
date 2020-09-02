// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.l2j.commons.util.EmptyQueue;
import java.util.concurrent.PriorityBlockingQueue;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.Queue;
import java.util.Map;

public class ListenersContainer
{
    private volatile Map<EventType, Queue<AbstractEventListener>> _listeners;
    
    public ListenersContainer() {
        this._listeners = null;
    }
    
    public AbstractEventListener addListener(final AbstractEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null!");
        }
        this.getListeners().computeIfAbsent(listener.getType(), k -> new PriorityBlockingQueue()).add(listener);
        return listener;
    }
    
    public AbstractEventListener removeListener(final AbstractEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null!");
        }
        if (this._listeners == null) {
            throw new NullPointerException("Listeners container is not initialized!");
        }
        if (!this._listeners.containsKey(listener.getType())) {
            throw new IllegalAccessError(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/events/EventType;)Ljava/lang/String;, listener.getType()));
        }
        this._listeners.get(listener.getType()).remove(listener);
        return listener;
    }
    
    public Queue<AbstractEventListener> getListeners(final EventType type) {
        return (this._listeners != null && this._listeners.containsKey(type)) ? ((Queue<AbstractEventListener>)this._listeners.get(type)) : EmptyQueue.emptyQueue();
    }
    
    public void removeListenerIf(final EventType type, final Predicate<? super AbstractEventListener> filter) {
        this.getListeners(type).stream().filter((Predicate<? super Object>)filter).forEach(AbstractEventListener::unregisterMe);
    }
    
    public void removeListenerIf(final Predicate<? super AbstractEventListener> filter) {
        if (this._listeners != null) {
            this.getListeners().values().forEach(queue -> queue.stream().filter(filter).forEach(AbstractEventListener::unregisterMe));
        }
    }
    
    public boolean hasListener(final EventType type) {
        return !this.getListeners(type).isEmpty();
    }
    
    private Map<EventType, Queue<AbstractEventListener>> getListeners() {
        if (this._listeners == null) {
            synchronized (this) {
                if (this._listeners == null) {
                    this._listeners = new ConcurrentHashMap<EventType, Queue<AbstractEventListener>>();
                }
            }
        }
        return this._listeners;
    }
}
