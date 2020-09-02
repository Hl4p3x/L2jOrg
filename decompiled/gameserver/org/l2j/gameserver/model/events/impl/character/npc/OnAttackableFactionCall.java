// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnAttackableFactionCall implements IBaseEvent
{
    private final Npc _npc;
    private final Npc _caller;
    private final Player _attacker;
    private final boolean _isSummon;
    
    public OnAttackableFactionCall(final Npc npc, final Npc caller, final Player attacker, final boolean isSummon) {
        this._npc = npc;
        this._caller = caller;
        this._attacker = attacker;
        this._isSummon = isSummon;
    }
    
    public Npc getNpc() {
        return this._npc;
    }
    
    public Npc getCaller() {
        return this._caller;
    }
    
    public Player getAttacker() {
        return this._attacker;
    }
    
    public boolean isSummon() {
        return this._isSummon;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_ATTACKABLE_FACTION_CALL;
    }
}
