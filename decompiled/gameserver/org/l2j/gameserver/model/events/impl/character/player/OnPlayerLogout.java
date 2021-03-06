// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerLogout implements IBaseEvent
{
    private final Player _activeChar;
    
    public OnPlayerLogout(final Player activeChar) {
        this._activeChar = activeChar;
    }
    
    public Player getPlayer() {
        return this._activeChar;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_LOGOUT;
    }
}
