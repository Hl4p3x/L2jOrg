// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnNpcCanBeSeen implements IBaseEvent
{
    private final Npc _npc;
    private final Player _activeChar;
    
    public OnNpcCanBeSeen(final Npc npc, final Player activeChar) {
        this._npc = npc;
        this._activeChar = activeChar;
    }
    
    public Npc getNpc() {
        return this._npc;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_CAN_BE_SEEN;
    }
}
