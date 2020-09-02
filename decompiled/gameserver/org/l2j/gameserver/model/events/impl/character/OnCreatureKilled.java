// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnCreatureKilled implements IBaseEvent
{
    private final Creature _attacker;
    private final Creature _target;
    
    public OnCreatureKilled(final Creature attacker, final Creature target) {
        this._attacker = attacker;
        this._target = target;
    }
    
    public final Creature getAttacker() {
        return this._attacker;
    }
    
    public final Creature getTarget() {
        return this._target;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_KILLED;
    }
}
