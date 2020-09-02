// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnNpcMoveFinished implements IBaseEvent
{
    private final Npc _npc;
    
    public OnNpcMoveFinished(final Npc npc) {
        this._npc = npc;
    }
    
    public Npc getNpc() {
        return this._npc;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_MOVE_FINISHED;
    }
}
