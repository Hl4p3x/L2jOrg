// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerPvPKill implements IBaseEvent
{
    private final Player _activeChar;
    private final Player _target;
    
    public OnPlayerPvPKill(final Player activeChar, final Player target) {
        this._activeChar = activeChar;
        this._target = target;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Player getTarget() {
        return this._target;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PVP_KILL;
    }
}
