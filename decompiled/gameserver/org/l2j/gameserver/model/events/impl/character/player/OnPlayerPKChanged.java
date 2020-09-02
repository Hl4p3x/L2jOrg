// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerPKChanged implements IBaseEvent
{
    private final Player _activeChar;
    private final int _oldPoints;
    private final int _newPoints;
    
    public OnPlayerPKChanged(final Player activeChar, final int oldPoints, final int newPoints) {
        this._activeChar = activeChar;
        this._oldPoints = oldPoints;
        this._newPoints = newPoints;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public int getOldPoints() {
        return this._oldPoints;
    }
    
    public int getNewPoints() {
        return this._newPoints;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_PK_CHANGED;
    }
}
