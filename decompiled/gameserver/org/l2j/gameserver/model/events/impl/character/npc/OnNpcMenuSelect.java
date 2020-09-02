// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnNpcMenuSelect implements IBaseEvent
{
    private final Player _activeChar;
    private final Npc _npc;
    private final int _ask;
    private final int _reply;
    
    public OnNpcMenuSelect(final Player activeChar, final Npc npc, final int ask, final int reply) {
        this._activeChar = activeChar;
        this._npc = npc;
        this._ask = ask;
        this._reply = reply;
    }
    
    public Player getTalker() {
        return this._activeChar;
    }
    
    public Npc getNpc() {
        return this._npc;
    }
    
    public int getAsk() {
        return this._ask;
    }
    
    public int getReply() {
        return this._reply;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_MENU_SELECT;
    }
}
