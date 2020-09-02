// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnPlayerLevelChanged implements IBaseEvent
{
    private final Player _activeChar;
    private final int _oldLevel;
    private final int _newLevel;
    
    public OnPlayerLevelChanged(final Player activeChar, final int oldLevel, final int newLevel) {
        this._activeChar = activeChar;
        this._oldLevel = oldLevel;
        this._newLevel = newLevel;
    }
    
    public Player getActiveChar() {
        return this._activeChar;
    }
    
    public int getOldLevel() {
        return this._oldLevel;
    }
    
    public int getNewLevel() {
        return this._newLevel;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_LEVEL_CHANGED;
    }
}
