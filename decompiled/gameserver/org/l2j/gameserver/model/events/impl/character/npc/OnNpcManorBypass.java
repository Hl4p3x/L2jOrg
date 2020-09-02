// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public final class OnNpcManorBypass implements IBaseEvent
{
    private final Player _activeChar;
    private final Npc _target;
    private final int _request;
    private final int _manorId;
    private final boolean _nextPeriod;
    
    public OnNpcManorBypass(final Player activeChar, final Npc target, final int request, final int manorId, final boolean nextPeriod) {
        this._activeChar = activeChar;
        this._target = target;
        this._request = request;
        this._manorId = manorId;
        this._nextPeriod = nextPeriod;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Npc getTarget() {
        return this._target;
    }
    
    public int getRequest() {
        return this._request;
    }
    
    public int getManorId() {
        return this._manorId;
    }
    
    public boolean isNextPeriod() {
        return this._nextPeriod;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_NPC_MANOR_BYPASS;
    }
}
