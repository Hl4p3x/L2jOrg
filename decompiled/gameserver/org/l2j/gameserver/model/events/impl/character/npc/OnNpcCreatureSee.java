// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnNpcCreatureSee implements IBaseEvent
{
    private final Npc _npc;
    private final Creature _creature;
    private final boolean _isSummon;
    
    public OnNpcCreatureSee(final Npc npc, final Creature creature, final boolean isSummon) {
        this._npc = npc;
        this._creature = creature;
        this._isSummon = isSummon;
    }
    
    public final Npc getNpc() {
        return this._npc;
    }
    
    public final Creature getCreature() {
        return this._creature;
    }
    
    public boolean isSummon() {
        return this._isSummon;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_CREATURE_SEE;
    }
}
