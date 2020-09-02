// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import java.util.ArrayList;
import java.util.List;

public class NextAction
{
    private List<CtrlEvent> _events;
    private List<CtrlIntention> _intentions;
    private NextActionCallback _callback;
    
    public NextAction(final List<CtrlEvent> events, final List<CtrlIntention> intentions, final NextActionCallback callback) {
        this._events = events;
        this._intentions = intentions;
        this.setCallback(callback);
    }
    
    public NextAction(final CtrlEvent event, final CtrlIntention intention, final NextActionCallback callback) {
        if (this._events == null) {
            this._events = new ArrayList<CtrlEvent>();
        }
        if (this._intentions == null) {
            this._intentions = new ArrayList<CtrlIntention>();
        }
        if (event != null) {
            this._events.add(event);
        }
        if (intention != null) {
            this._intentions.add(intention);
        }
        this.setCallback(callback);
    }
    
    public void doAction() {
        if (this._callback != null) {
            this._callback.doWork();
        }
    }
    
    public List<CtrlEvent> getEvents() {
        if (this._events == null) {
            this._events = new ArrayList<CtrlEvent>();
        }
        return this._events;
    }
    
    public void setEvents(final ArrayList<CtrlEvent> event) {
        this._events = event;
    }
    
    public void addEvent(final CtrlEvent event) {
        if (this._events == null) {
            this._events = new ArrayList<CtrlEvent>();
        }
        if (event != null) {
            this._events.add(event);
        }
    }
    
    public void removeEvent(final CtrlEvent event) {
        if (this._events == null) {
            return;
        }
        this._events.remove(event);
    }
    
    public NextActionCallback getCallback() {
        return this._callback;
    }
    
    public void setCallback(final NextActionCallback callback) {
        this._callback = callback;
    }
    
    public List<CtrlIntention> getIntentions() {
        if (this._intentions == null) {
            this._intentions = new ArrayList<CtrlIntention>();
        }
        return this._intentions;
    }
    
    public void setIntentions(final ArrayList<CtrlIntention> intentions) {
        this._intentions = intentions;
    }
    
    public void addIntention(final CtrlIntention intention) {
        if (this._intentions == null) {
            this._intentions = new ArrayList<CtrlIntention>();
        }
        if (intention != null) {
            this._intentions.add(intention);
        }
    }
    
    public void removeIntention(final CtrlIntention intention) {
        if (this._intentions == null) {
            return;
        }
        this._intentions.remove(intention);
    }
    
    public interface NextActionCallback
    {
        void doWork();
    }
}
