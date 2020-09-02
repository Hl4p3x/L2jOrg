// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnNpcEventReceived implements IBaseEvent
{
    private final String _eventName;
    private final Npc _sender;
    private final Npc _receiver;
    private final WorldObject _reference;
    
    public OnNpcEventReceived(final String eventName, final Npc sender, final Npc receiver, final WorldObject reference) {
        this._eventName = eventName;
        this._sender = sender;
        this._receiver = receiver;
        this._reference = reference;
    }
    
    public String getEventName() {
        return this._eventName;
    }
    
    public Npc getSender() {
        return this._sender;
    }
    
    public Npc getReceiver() {
        return this._receiver;
    }
    
    public WorldObject getReference() {
        return this._reference;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_EVENT_RECEIVED;
    }
}
