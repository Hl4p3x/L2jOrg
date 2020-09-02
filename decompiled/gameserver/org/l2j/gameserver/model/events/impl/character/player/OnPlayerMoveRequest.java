// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerMoveRequest implements IBaseEvent
{
    private final Player _activeChar;
    private final Location _location;
    
    public OnPlayerMoveRequest(final Player activeChar, final Location loc) {
        this._activeChar = activeChar;
        this._location = loc;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public Location getLocation() {
        return this._location;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_MOVE_REQUEST;
    }
}
