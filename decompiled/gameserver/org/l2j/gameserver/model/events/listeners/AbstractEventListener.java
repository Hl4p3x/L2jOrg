// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.listeners;

import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenersContainer;

public abstract class AbstractEventListener implements Comparable<AbstractEventListener>
{
    private final ListenersContainer _container;
    private final EventType _type;
    private final Object _owner;
    private int _priority;
    
    public AbstractEventListener(final ListenersContainer container, final EventType type, final Object owner) {
        this._priority = 0;
        this._container = container;
        this._type = type;
        this._owner = owner;
    }
    
    public ListenersContainer getContainer() {
        return this._container;
    }
    
    public EventType getType() {
        return this._type;
    }
    
    public Object getOwner() {
        return this._owner;
    }
    
    public int getPriority() {
        return this._priority;
    }
    
    public void setPriority(final int priority) {
        this._priority = priority;
    }
    
    public abstract <R extends AbstractEventReturn> R executeEvent(final IBaseEvent event, final Class<R> returnBackClass);
    
    public void unregisterMe() {
        this._container.removeListener(this);
    }
    
    @Override
    public int compareTo(final AbstractEventListener o) {
        return Integer.compare(o.getPriority(), this.getPriority());
    }
}
