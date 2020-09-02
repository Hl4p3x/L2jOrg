// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.enums.TrapAction;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnTrapAction implements IBaseEvent
{
    private final Trap _trap;
    private final Creature _trigger;
    private final TrapAction _action;
    
    public OnTrapAction(final Trap trap, final Creature trigger, final TrapAction action) {
        this._trap = trap;
        this._trigger = trigger;
        this._action = action;
    }
    
    public Trap getTrap() {
        return this._trap;
    }
    
    public Creature getTrigger() {
        return this._trigger;
    }
    
    public TrapAction getAction() {
        return this._action;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_TRAP_ACTION;
    }
}
