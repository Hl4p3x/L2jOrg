// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnAttackableHate implements IBaseEvent
{
    private final Attackable _npc;
    private final Player _activeChar;
    private final boolean _isSummon;
    
    public OnAttackableHate(final Attackable npc, final Player activeChar, final boolean isSummon) {
        this._npc = npc;
        this._activeChar = activeChar;
        this._isSummon = isSummon;
    }
    
    public final Attackable getNpc() {
        return this._npc;
    }
    
    public final Player getActiveChar() {
        return this._activeChar;
    }
    
    public boolean isSummon() {
        return this._isSummon;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_HATE;
    }
}
