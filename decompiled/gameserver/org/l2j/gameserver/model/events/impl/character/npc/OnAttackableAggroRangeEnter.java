// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnAttackableAggroRangeEnter implements IBaseEvent
{
    private final Npc _npc;
    private final Player _activeChar;
    private final boolean _isSummon;
    
    public OnAttackableAggroRangeEnter(final Npc npc, final Player attacker, final boolean isSummon) {
        this._npc = npc;
        this._activeChar = attacker;
        this._isSummon = isSummon;
    }
    
    public Npc getNpc() {
        return this._npc;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public boolean isSummon() {
        return this._isSummon;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_ATTACKABLE_AGGRO_RANGE_ENTER;
    }
}
