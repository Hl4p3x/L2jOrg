// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnAttackableKill implements IBaseEvent
{
    private final Player _attacker;
    private final Attackable _target;
    private final boolean _isSummon;
    private final Object _payload;
    
    public OnAttackableKill(final Player attacker, final Attackable target, final boolean isSummon) {
        this._attacker = attacker;
        this._target = target;
        this._isSummon = isSummon;
        this._payload = null;
    }
    
    public OnAttackableKill(final Player attacker, final Attackable target, final boolean isSummon, final Object payload) {
        this._attacker = attacker;
        this._target = target;
        this._isSummon = isSummon;
        this._payload = payload;
    }
    
    public final Player getAttacker() {
        return this._attacker;
    }
    
    public final Attackable getTarget() {
        return this._target;
    }
    
    public final boolean isSummon() {
        return this._isSummon;
    }
    
    public final Object getPayload() {
        return this._payload;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_ATTACKABLE_KILL;
    }
}
