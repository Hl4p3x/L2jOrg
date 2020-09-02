// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureTeleported implements IBaseEvent
{
    private final Creature _creature;
    
    public OnCreatureTeleported(final Creature creature) {
        this._creature = creature;
    }
    
    public Creature getCreature() {
        return this._creature;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_TELEPORTED;
    }
}
