// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureSee implements IBaseEvent
{
    private final Creature _seer;
    private final Creature _seen;
    
    public OnCreatureSee(final Creature seer, final Creature seen) {
        this._seer = seer;
        this._seen = seen;
    }
    
    public final Creature getSeer() {
        return this._seer;
    }
    
    public final Creature getSeen() {
        return this._seen;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_SEE;
    }
}
